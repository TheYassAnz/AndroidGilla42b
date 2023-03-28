package com.example.androidgilla42b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EvenementsAdapter extends RecyclerView.Adapter<EvenementsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void OnItemClick(Evenement item);
    }

    private List<Evenement> mEvenements;
    private OnItemClickListener mListener;

    public EvenementsAdapter(List<Evenement> evenements, OnItemClickListener listener) {
        mEvenements = evenements;
        mListener = listener;
    }

    // Associe une référence à chaque élement de la vue pour un accès rapide par cache
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Le porteur de vue (ViewHolder) associe une variable à chaque élément d'une rangée
        public TextView aidTextView;
        public TextView date_timeTextView;
        public TextView titleTextView;
        public TextView descriptionTextView;

        // Constructor of the entire item row
        // doing the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            aidTextView = (TextView) itemView.findViewById(R.id.aid);
            date_timeTextView = (TextView) itemView.findViewById(R.id.date_time);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description);
        }

        public void bind(final Evenement item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.OnItemClick(item));
        }
    }

    // Crée le template (layout) à partir du XML and retourne le porteur (holder)
    @Override
    public EvenementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View evenementView = inflater.inflate(R.layout.item_evenement, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(evenementView);
        return viewHolder;
    }

    // Remplit les données dans l'élément de la liste par le porteur (holder)
    @Override
    public void onBindViewHolder(EvenementsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Evenement evenement = mEvenements.get(position);
        holder.bind(mEvenements.get(position), mListener);

        // Set item views based on your views and data model
        TextView textView1 = holder.aidTextView;
        textView1.setText(evenement.getId());

        TextView textView2 = holder.date_timeTextView;
        textView2.setText(evenement.getDateTime());

        TextView textView3 = holder.titleTextView;
        textView3.setText(evenement.getTitle());

        TextView textView4 = holder.descriptionTextView;
        textView4.setText(evenement.getDescription());
    }

    // Retourne le nombre total d'élément dans la liste
    @Override
    public int getItemCount() {
        return mEvenements.size();
    }

    // Lie l'adaptateur au RecyclerView
    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}