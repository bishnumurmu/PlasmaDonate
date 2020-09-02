package com.project.plasmadonate.patients;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.plasmadonate.R;
import com.project.plasmadonate.messages.ChatsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPatient extends RecyclerView.Adapter<AdapterPatient.Viewholder> {

    private List<ModelPatient> categoryModelList;
    String contactDB;

    public AdapterPatient(List<ModelPatient> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_patients, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        //get data for phone number
        //contactDB = categoryModelList.get(position).getContact();

        holder.setData(categoryModelList.get(position).getCity(), categoryModelList.get(position).getName(), categoryModelList.get(position).getContact());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView title;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
        }

        private void setData(final String city, final String title, final String contact){

            //Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    Intent setIntent = new Intent(itemView.getContext(), SetsActivity.class);
                    setIntent.putExtra("title", title);
                    setIntent.putExtra("sets", sets);
                    itemView.getContext().startActivity(setIntent);
                    */
                    //Toast.makeText(itemView.getContext(), "Hello There, I am"+title, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(itemView.getContext(), ChatsActivity.class);
                    intent.putExtra("contact", contact);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }

}