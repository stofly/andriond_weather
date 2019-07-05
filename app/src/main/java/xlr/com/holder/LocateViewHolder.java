package xlr.com.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import xlr.com.sbcweather.R;


/**
 * Created by baisoo on 16/9/24.
 */

public class LocateViewHolder extends RecyclerView.ViewHolder {

    public TextView mTvLocatedCity;

    public LocateViewHolder(View itemView) {
        super(itemView);
        mTvLocatedCity = (TextView) itemView.findViewById(R.id.tv_located_city);
    }
}
