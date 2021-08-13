package ng.assist.UIs.ViewModel;

public class RecipientUserInfo {
    private String userEmail;
    private String userImageUrl;
    private String userFirstname;
    private String userLastname;

    public RecipientUserInfo(String userEmail, String userImageUrl, String userFirstname, String userLastname){
        this.userEmail = userEmail;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userImageUrl = userImageUrl;
    }


    public String getUserFirstname() {
        return userFirstname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserLastname() {
        return userLastname;
    }


}