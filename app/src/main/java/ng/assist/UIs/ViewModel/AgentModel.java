package ng.assist.UIs.ViewModel;

public class AgentModel {
    private String agentId;
    private String agentFirstname;
    private String agentLastName;
    private String agentPhone;
    private String agentPicUrl;

    public AgentModel(String agentId, String agentFirstname, String agentLastName, String agentPhone, String agentPicUrl){
        this.agentId = agentId;
        this.agentFirstname = agentFirstname;
        this.agentLastName = agentLastName;
        this.agentPhone = agentPhone;
        this.agentPicUrl = agentPicUrl;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAgentFirstname() {
        return agentFirstname;
    }

    public String getAgentLastName() {
        return agentLastName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public String getAgentPicUrl() {
        return agentPicUrl;
    }

}
