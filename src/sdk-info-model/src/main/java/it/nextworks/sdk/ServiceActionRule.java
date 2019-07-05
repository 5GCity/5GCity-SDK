package it.nextworks.sdk;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;


/**
 * ServiceActionRule
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "actionsId",
    "conditions",
    "operator"
})
@Entity
public class ServiceActionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> actionsId = new ArrayList<String>();

    @OneToMany(mappedBy = "serviceActionRule", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<RuleCondition> conditions = new HashSet<>();

    private Operator operator = Operator.fromValue("and");

    @ManyToOne
    private SdkService sdkService;

    public ServiceActionRule(){
        //JPA only
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("actionsName")
    public List<String> getActionsId() {
        return actionsId;
    }

    @JsonProperty("actionsName")
    public void setActionsId(List<String> actionsId) {
        this.actionsId = actionsId;
    }

    @JsonProperty("conditions")
    public Set<RuleCondition> getConditions() {
        return conditions;
    }

    @JsonProperty("conditions")
    public void setConditions(Set<RuleCondition> conditions) {

        this.conditions.clear();
        this.conditions.addAll(conditions);

        for (RuleCondition sa : this.conditions) {
            sa.setServiceActionRule(this);
        }
    }

    @JsonProperty("operator")
    public Operator getOperator() {
        return operator;
    }

    @JsonProperty("operator")
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @JsonIgnore
    public SdkService getSdkService() {
        return sdkService;
    }

    @JsonIgnore
    public void setSdkService(SdkService sdkService) {
        this.sdkService = sdkService;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ServiceActionRule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("actionsId");
        sb.append('=');
        sb.append(((this.actionsId == null)?"<null>":this.actionsId));
        sb.append(',');
        sb.append("conditions");
        sb.append('=');
        sb.append(((this.conditions == null)?"<null>":this.conditions));
        sb.append(',');
        sb.append("operator");
        sb.append('=');
        sb.append(((this.operator == null)?"<null>":this.operator));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.actionsId == null)? 0 :this.actionsId.hashCode()));
        result = ((result* 31)+((this.conditions == null)? 0 :this.conditions.hashCode()));
        result = ((result* 31)+((this.operator == null)? 0 :this.operator.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ServiceActionRule) == false) {
            return false;
        }
        ServiceActionRule rhs = ((ServiceActionRule) other);
        return ((((this.actionsId == rhs.actionsId)||((this.actionsId!= null)&&this.actionsId.equals(rhs.actionsId)))&&((this.conditions == rhs.conditions)||((this.conditions!= null)&&this.conditions.equals(rhs.conditions))))&&((this.operator == rhs.operator)||((this.operator!= null)&&this.operator.equals(rhs.operator))));
    }

    public enum Operator {

        AND("and"),
        OR("or");
        private final String value;
        private final static Map<String, Operator> CONSTANTS = new HashMap<String, Operator>();

        static {
            for (Operator c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Operator(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Operator fromValue(String value) {
            Operator constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @JsonIgnore
    public boolean isValid() {
        return actionsId != null
            && actionsId.size() > 0
            && conditions != null
            && conditions.size() > 0
            && conditions.stream().allMatch(RuleCondition::isValid);
    }
}
