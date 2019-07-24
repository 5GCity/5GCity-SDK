package it.nextworks.composer.adaptor.expression;

import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.Link;
import it.nextworks.sdk.SdkFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 12/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
class AdapterLink {

    static AdapterLinkBuilder builder(String serviceName, Map<Long, String> starting2name) {
        return new AdapterLinkBuilder(serviceName, starting2name);
    }

    String name;

    Map<Long, String> function2CpName;

    Set<Long> linkIds;

    private AdapterLink(String name, Map<Long, String> function2CpName, Set<Long> linkIds) {
        this.name = name;
        this.function2CpName = function2CpName;
        this.linkIds = linkIds;
    }

    static class AdapterLinkBuilder {

        private AdapterLinkBuilder(String serviceName, Map<Long, String> starting2name) {
            this.serviceName = serviceName;
            this.starting2name = starting2name;
        }

        String serviceName;

        Map<Long, String> starting2name;

        private Map<Long, Link> links = new HashMap<>();

        private Map<Long, Set<Long>> link2cp = new HashMap<>();

        private Map<Long, Long> cp2Link = new HashMap<>();

        private Map<Long, Long> cp2Int = new HashMap<>();

        private Map<Long, Long> int2Cp = new HashMap<>();

        private Map<Long, ConnectionPoint> cps = new HashMap<>();

        private Map<Long, List<FunctionCp>> link2Function = new HashMap<>();

        private Map<Long, Long> cp2Function = new HashMap<>();

        AdapterLinkBuilder addLink(Link link) {
            Long linkId = link.getId();
            if (links.containsKey(linkId)) {
                throw new IllegalArgumentException(String.format(
                    "Link %s already added",
                    linkId
                ));
            }
            links.put(linkId, link);
            link2cp.put(
                linkId,
                new HashSet<>()
            );

            for (ConnectionPoint cp : link.getConnectionPoints()) {
                Long cpId = cp.getId();
                link2cp.get(linkId).add(cpId);
                if (cp2Link.containsKey(cpId)) {
                    throw new IllegalStateException(String.format(
                        "Multiple links attached to CP %s: %s, %s",
                        cpId,
                        cp2Link.get(cpId),
                        linkId
                    ));
                }
                cp2Link.put(cpId, linkId);

                if (!this.cps.containsKey(cpId)) {
                    addCp(cp);
                }
            }
            return this;
        }

        AdapterLinkBuilder addCp(ConnectionPoint cp) {
            if (cps.containsKey(cp.getId())) {
                return this;
            }
            cps.put(cp.getId(), cp);

            // int CP id
            Long intCpId = cp.getInternalCpId();
            if (intCpId != null) {
                cp2Int.put(cp.getId(), intCpId);
                int2Cp.put(intCpId, cp.getId());
            }

            if (cp.getSdkFunction() != null) {
                SdkFunction function = cp.getSdkFunction();
                cp2Function.put(cp.getId(), function.getId());
            }
            return this;
        }

        private void addCpStackLinks(Long cpId, Set<Long> partial, boolean lower) {
            Map<Long, Long> cp2Other = lower ? cp2Int : int2Cp;
            Long otherCp = cp2Other.get(cpId);
            if (otherCp == null) {
                return;
            }
            Long otherLink = cp2Link.get(otherCp);
            if (otherLink != null) {
                partial.add(otherLink);
            }
            addCpStackLinks(otherCp, partial, lower);
        }

        private Set<Long> getLinksOnOtherSide(Long cpId) {
            Set<Long> output = new HashSet<>();

            // lower level links
            addCpStackLinks(cpId, output, true);

            // upper level links
            addCpStackLinks(cpId, output, false);
            return output;
        }

        private Set<Long> checkConnected(Long linkId, Set<Long> remaining) {
            if (!remaining.contains(linkId)) {
                throw new IllegalArgumentException(String.format(
                    "Starting link %s not in available links: %s",
                    linkId,
                    remaining
                ));
            }
            Set<Long> connectedComponent = new HashSet<>();
            Queue<Long> frontier = new LinkedList<>();
            frontier.add(linkId);
            connectedComponent.add(linkId);
            while (!frontier.isEmpty()) {
                Long current = frontier.remove();
                remaining.remove(current);
                for (Long cpId : link2cp.get(current)) {
                    Set<Long> candidates = getLinksOnOtherSide(cpId);
                    candidates.removeAll(connectedComponent);
                    frontier.addAll(candidates);
                    connectedComponent.addAll(candidates);
                }
            }
            return connectedComponent;
        }

        private void makeLink2Function() {
            for (Map.Entry<Long, Set<Long>> e : link2cp.entrySet()) {
                Long linkId = e.getKey();
                Set<Long> cpIds = e.getValue();
                for (Long cpId : cpIds) {
                    Long current = cp2Int.get(cpId);
                    while (current != null) {
                        Long function = cp2Function.get(current);
                        if (function != null) {
                            link2Function.putIfAbsent(linkId, new ArrayList<>());
                            link2Function.get(linkId).add(new FunctionCp(
                                function, cps.get(current).getName()
                            ));
                        }
                        current = cp2Int.get(current);
                    }
                }
            }
        }

        private AdapterLink makeLink(Long startLink, String linkName, Set<Long> remaining) {
            Set<Long> connectedComponent = checkConnected(startLink, remaining);

            Map<Long, String> linkFunctions = connectedComponent.stream()
                .map(l -> link2Function.get(l))  // get function set for the links in the conn-comp
                .filter(Objects::nonNull)  // skip null sets
                .flatMap(Collection::stream)  // flatten sets -> stream of FunctionCp
                .collect(Collectors.toMap(  // build name map
                    fcp -> fcp.functionId,
                    fcp -> fcp.cpName
                ));

            return new AdapterLink(
                linkName,
                linkFunctions,
                connectedComponent
            );
        }

        public Set<AdapterLink> build() {
            makeLink2Function();
            Set<AdapterLink> output = new HashSet<>();
            Set<Long> remaining = new HashSet<>(links.keySet());
            for (Map.Entry<Long, String> e : starting2name.entrySet()) {
                Long startLink = e.getKey();
                output.add(makeLink(
                    startLink,
                    e.getValue(),
                    remaining
                ));
            }

            int counter = 1;
            while (!remaining.isEmpty()) {
                Long startLink = remaining.iterator().next();
                output.add(makeLink(
                    startLink,
                    String.format("SDK-AUX-LINK-%d", counter),
                    remaining
                ));
                counter++;
            }
            return output;
        }
    }

    private static class FunctionCp {
        private final Long functionId;
        private final String cpName;

        private FunctionCp(Long functionId, String cpName) {
            this.functionId = functionId;
            this.cpName = cpName;
        }
    }
}
