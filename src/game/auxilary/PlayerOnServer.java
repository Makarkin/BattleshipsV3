package game.auxilary;

public class PlayerOnServer {
    private String nickName;
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNickName() {
        return nickName;
    }

    public String getStatus() {
        return status;
    }

    public PlayerOnServer(String nickName, String status) {
        this.nickName = nickName;
        this.status = status;
    }
}
