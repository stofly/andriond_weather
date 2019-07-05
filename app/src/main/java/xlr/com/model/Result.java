package xlr.com.model;

/**
 * @author 青铜骑士
 * @ClassName: Result
 * @ProjectName sbcweather
 * @Description: TODO
 * @date 2019/6/1818:33
 */
import java.util.List;
public class Result
{
    private List<Future> future;
    private Sk sk;
    private Today today;

    public void setFuture(List<Future> future){
        this.future = future;
    }
    public List<Future> getFuture(){
        return this.future;
    }
    public void setSk(Sk sk){
        this.sk = sk;
    }
    public Sk getSk(){
        return this.sk;
    }
    public void setToday(Today today){
        this.today = today;
    }
    public Today getToday(){
        return this.today;
    }
}
