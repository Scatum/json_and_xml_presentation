package com.mix.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mix.R;
import com.mix.domain.Feed;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder> {

    OnItemClickListener onItemClickListener;

    private List<Feed> data = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;

    public FeedsAdapter(Context context, OnItemClickListener listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onItemClickListener = listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_feed_item, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Feed> newData) {
        data.addAll(newData);
        Collections.sort(data);
        notifyDataSetChanged();
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

        private TextView txtFeedTitle;
        private LinearLayout optionalMenu;
        private TextView descriptionTV;
        private TextView publishedAtTV;
        private TextView dataType;
        private ImageView feedImage;


        FeedViewHolder(View itemView) {
            super(itemView);

            txtFeedTitle = itemView.findViewById(R.id.text_view_feed_title);
            optionalMenu = itemView.findViewById(R.id.optional_menu);
            dataType = itemView.findViewById(R.id.data_type);
            publishedAtTV = itemView.findViewById(R.id.text_view_published_at);
            publishedAtTV.setSelected(true);
            descriptionTV = itemView.findViewById(R.id.description);
            feedImage = itemView.findViewById(R.id.feedImageView);
        }

        void bind(final Feed feed) {
            if (feed != null) {
                Picasso.get()
                        .load(feed.getImageThumbnail())
                        .into(feedImage);

                txtFeedTitle.setText(feed.getTitle());
                publishedAtTV.setText(feed.getDate().toString());
                descriptionTV.setText(feed.getDescription());
                dataType.setText(String.valueOf(feed.getDataType()));
                itemView.setOnClickListener(v -> {
                    if (onItemClickListener != null)
                        onItemClickListener.openWebView(feed);
                });
                optionalMenu.setOnClickListener(v->{
                    if (onItemClickListener != null)
                        onItemClickListener.saveFeedInfo(feed, optionalMenu);
                });

            }
        }

    }

   public interface OnItemClickListener {
        void openWebView(Feed feed);

        void saveFeedInfo(Feed feed, View view);
    }

}
