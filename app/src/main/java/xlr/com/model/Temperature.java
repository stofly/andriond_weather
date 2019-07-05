package xlr.com.model;

/**
 * @author 青铜骑士
 * @ClassName: Temperature
 * @ProjectName sbcweather
 * @Description: TODO
 * @date 2019/6/1921:18
 */
public class Temperature {
    private Integer id;
    private String cname;
    private String cweather;
    private String ctemperature;

    public Temperature( String cname, String cweather, String ctemperature) {
        this.cname = cname;
        this.cweather = cweather;
        this.ctemperature = ctemperature;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCweather() {
        return cweather;
    }

    public void setCweather(String cweather) {
        this.cweather = cweather;
    }

    public String getCtemperature() {
        return ctemperature;
    }

    public void setCtemperature(String ctemperature) {
        this.ctemperature = ctemperature;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "id=" + id +
                ", cname='" + cname + '\'' +
                ", cweather='" + cweather + '\'' +
                ", ctemperature='" + ctemperature + '\'' +
                '}';
    }
}
