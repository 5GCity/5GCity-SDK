/*
package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.ws.Service;


/**
 * ReconfigureAction
 * <p>
 * 
 * 
 */
/*
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "extMonitoringParameters",
    "intMonitoringParameters",
    "actionRules"
})
public class ReconfigureAction extends ServiceAction{

    @OneToMany(mappedBy = "reconfigureActionExt", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> extMonitoringParameters = new HashSet<>();

    @OneToMany(mappedBy = "reconfigureActionInt", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> intMonitoringParameters = new HashSet<>();

    @OneToMany(mappedBy = "reconfigureAction", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ServiceActionRule> actionRules = new HashSet<>();

    public ReconfigureAction(){
        //JPA only
    }

    @JsonProperty("extMonitoringParameters")
    public Set<MonitoringParameter> getExtMonitoringParameters() {
        return extMonitoringParameters;
    }

    @JsonProperty("extMonitoringParameters")
    public void setExtMonitoringParameters(Set<MonitoringParameter> extMonitoringParameters) {
        //this.extMonitoringParameters = extMonitoringParameters;
        this.extMonitoringParameters.clear();
        this.extMonitoringParameters.addAll(extMonitoringParameters);

        for (MonitoringParameter sa : this.extMonitoringParameters) {
            sa.setReconfigureActionExt(this);
        }
    }

    @JsonProperty("intMonitoringParameters")
    public Set<MonitoringParameter> getIntMonitoringParameters() {
        return intMonitoringParameters;
    }

    @JsonProperty("intMonitoringParameters")
    public void setIntMonitoringParameters(Set<MonitoringParameter> intMonitoringParameters) {
        //this.intMonitoringParameters = intMonitoringParameters;

        this.intMonitoringParameters.clear();
        this.intMonitoringParameters.addAll(intMonitoringParameters);

        for (MonitoringParameter sa : this.intMonitoringParameters) {
            sa.setReconfigureActionInt(this);
        }
    }

    @JsonProperty("actionRules")
    public Set<ServiceActionRule> getActionRules() {
        return actionRules;
    }

    @JsonProperty("actionRules")
    public void setActionRules(Set<ServiceActionRule> actionRules) {

        //this.actionRules = actionRules;
        this.actionRules.clear();
        this.actionRules.addAll(actionRules);

        for (ServiceActionRule sa : this.actionRules) {
            sa.setReconfigureAction(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ReconfigureAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("extMonitoringParameters");
        sb.append('=');
        sb.append(((this.extMonitoringParameters == null)?"<null>":this.extMonitoringParameters));
        sb.append(',');
        sb.append("intMonitoringParameters");
        sb.append('=');
        sb.append(((this.intMonitoringParameters == null)?"<null>":this.intMonitoringParameters));
        sb.append(',');
        sb.append("actionRules");
        sb.append('=');
        sb.append(((this.actionRules == null)?"<null>":this.actionRules));
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
        result = ((result* 31)+((this.extMonitoringParameters == null)? 0 :this.extMonitoringParameters.hashCode()));
        result = ((result* 31)+((this.actionRules == null)? 0 :this.actionRules.hashCode()));
        result = ((result* 31)+((this.intMonitoringParameters == null)? 0 :this.intMonitoringParameters.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ReconfigureAction) == false) {
            return false;
        }
        ReconfigureAction rhs = ((ReconfigureAction) other);
        return ((((this.extMonitoringParameters == rhs.extMonitoringParameters)||((this.extMonitoringParameters!= null)&&this.extMonitoringParameters.equals(rhs.extMonitoringParameters)))&&((this.actionRules == rhs.actionRules)||((this.actionRules!= null)&&this.actionRules.equals(rhs.actionRules))))&&((this.intMonitoringParameters == rhs.intMonitoringParameters)||((this.intMonitoringParameters!= null)&&this.intMonitoringParameters.equals(rhs.intMonitoringParameters))));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && validateMonitoringParameters(intMonitoringParameters)
            && validateMonitoringParameters(extMonitoringParameters)
            && actionRules.stream().allMatch(ServiceActionRule::isValid);
    }

    private boolean validateMonitoringParameters(Set<MonitoringParameter> param){
        if(param != null)
            return param.stream().allMatch(MonitoringParameter::isValid);
        else
            return true;
    }
}
*/