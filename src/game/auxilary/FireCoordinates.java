package game.auxilary;

public class FireCoordinates {
    private int i;
    private int j;
    private String to;

    public FireCoordinates(int i, int j, String to) {
        this.i = i;
        this.j = j;
        this.to = to;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
