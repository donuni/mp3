package com.example.user.mymp3_2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {
    Context context;
    int layout;
    private ArrayList<MainData> arrayList;
    String selectFileNameString;

    public MainAdapter(Context context, int layout, ArrayList<MainData> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    //처음생성될때와같은생명주기와같다고생각할것
    @NonNull
    @Override
    public MainAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    //실제추가되었을때
    @Override
    public void onBindViewHolder(@NonNull final MainAdapter.CustomViewHolder customViewHolder, final int i) {

        customViewHolder.tvFileName.setText(arrayList.get(i).getFileName());
        customViewHolder.tvSinger.setText(arrayList.get(i).getSinger());
        customViewHolder.tvGenre.setText(arrayList.get(i).getGenre());
        customViewHolder.tvStar.setText(arrayList.get(i).getStar());
        customViewHolder.tvFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(customViewHolder.getAdapterPosition());
            }
        });

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),arrayList.get(i).getFileName()+"재생",Toast.LENGTH_SHORT).show();

                //
                selectFileNameString = arrayList.get(i).getFileName();
                MainActivity.btnPlay.callOnClick();

                //
            }
        });
    }

    @Override
    public int getItemCount() {
        return   ((arrayList != null) ? arrayList.size() : 0 );
    }

    public void remove(int position){
        try {

            MainData removeMainData=arrayList.get(position);
            MainActivity.myDBHelper.delete(removeMainData.getFileName());
            arrayList.remove(position);
        }catch(IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvFileName;
        protected TextView tvSinger;
        protected TextView tvGenre;
        protected TextView tvStar;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
            this.tvSinger = itemView.findViewById(R.id.tvSinger);
            this.tvGenre = itemView.findViewById(R.id.tvGenre);
            this.tvStar = itemView.findViewById(R.id.tvStar);
        }
    }
}
