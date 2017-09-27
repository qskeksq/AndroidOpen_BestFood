package com.example.administrator.bestfood.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.lib.GoLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2017-09-27.
 */

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private int resource;
    private List<FoodInfoItem> itemList;
    int MAX_LENGTH_DESCRIPTION = 50;

    /**
     * 어댑터 생성자
     * @param context 컨텍스트 객체
     * @param resource 아이템을 보여주기 위해 사용할 리소스 아이디
     * @param itemList 아이템 리스트
     */
    public MapListAdapter(Context context, int resource, List<FoodInfoItem> itemList) {
        this.context = context;
        this.resource = resource;
        this.itemList = itemList;
    }

    /**
     * 새로운 아이템 리스트를 설정한다.
     * @param itemList 새로운 아이템 리스트
     */
    public void setItemList(List<FoodInfoItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     * 아이템 크기를 반환한다.
     * @return 아이템 크기
     */
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    /**
     * 뷰홀더(ViewHolder)를 생성하기 위해 자동으로 호출된다.
     * @param parent 부모 뷰그룹
     * @param viewType 새로운 뷰의 뷰타입
     * @return 뷰홀더 객체
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ViewHolder(v);
    }

    /**
     * 뷰홀더(ViewHolder)와 아이템을 리스트 위치에 따라 연동한다.
     * @param holder 뷰홀더 객체
     * @param position 리스트 위치
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FoodInfoItem item = itemList.get(position);

        int meter = (int) item.userDistanceMeter;

        if (meter == 0) {
            holder.distanceMeter.setText("");
        } else if (meter < 1000) {
            holder.distanceMeter.setText(meter + context.getResources().getString(R.string.unit_m));
        } else {
            holder.distanceMeter.setText( (meter / 1000)
                    + context.getResources().getString(R.string.unit_km));
        }

        holder.name.setText(item.name);
        holder.description.setText(StringLib.getInstance().getSubString(context,
                item.description, MAX_LENGTH_DESCRIPTION));

        setImage(holder.imageView, item.imageFilename);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoLib.getInstance().goBestFoodInfoActivity(context, item.seq);
            }
        });
    }

    /**
     * 이미지를 설정한다.
     * @param imageView  이미지를 설정할 뷰
     * @param fileName 이미지 파일이름
     */
    private void setImage(ImageView imageView, String fileName) {

        if (StringLib.getInstance().isBlank(fileName)) {
            Picasso.with(context).load(R.drawable.bg_bestfood_drawer).into(imageView);
        } else {
            Picasso.with(context).load(IRemoteService.IMAGE_URL + fileName).into(imageView);
        }
    }

    /**
     * 아이템을 보여주기 위한 뷰홀더 클래스
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView distanceMeter;
        TextView name;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            distanceMeter = (TextView) itemView.findViewById(R.id.distance_meter);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
