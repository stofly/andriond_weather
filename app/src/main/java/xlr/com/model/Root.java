package xlr.com.model;

/**
 * @author 青铜骑士
 * @ClassName: Root
 * @ProjectName sbcweather
 * @Description: TODO
 * @date 2019/6/1818:43
 */
public class Root
{
    private int error_code;

    private String reason;

    private Result result;

    private String resultcode;

    public void setError_code(int error_code){
        this.error_code = error_code;
    }
    public int getError_code(){
        return this.error_code;
    }
    public void setReason(String reason){
        this.reason = reason;
    }
    public String getReason(){
        return this.reason;
    }
    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }
    public void setResultcode(String resultcode){
        this.resultcode = resultcode;
    }
    public String getResultcode(){
        return this.resultcode;
    }

    @Override
    public String toString() {
        return "Root{" +
                "error_code=" + error_code +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                ", resultcode='" + resultcode + '\'' +
                '}';
    }
}
