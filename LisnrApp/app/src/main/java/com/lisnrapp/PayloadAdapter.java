package com.lisnrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PayloadAdapter extends RecyclerView.Adapter<PayloadAdapter.PayloadViewHolder> {

    private Context mApplicationContext; // Application context is needed to get string and color resources

    private ArrayList<Payload> mData;
    private RecyclerView mView;

    private int mExpandedPayloadIndex = -1;

    private ArrayList<String> mRestrictedDisplayProfiles = new ArrayList<>();

    // Provide a reference to the views for each data item
    static class PayloadViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout dataItem;
        PayloadViewHolder(ConstraintLayout v) {
            super(v);
            dataItem = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public PayloadAdapter(Context context, ArrayList<Payload> data, RecyclerView view) {
        mApplicationContext = context.getApplicationContext();
        mData = data;
        mView = view;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PayloadAdapter.PayloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        ConstraintLayout payload = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_receive_payload, parent,
                false);

        // The extra info should be hidden by default
        payload.findViewById(R.id.constraint_hidden_info).setVisibility(View.GONE);

        return new PayloadViewHolder(payload);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final PayloadViewHolder holder, final int position) {
        final Payload payload = mData.get(position);
        final ConstraintLayout hiddenInfo = holder.dataItem.findViewById(R.id.constraint_hidden_info);
        final ImageView arrow = holder.dataItem.findViewById(R.id.image_arrow);

        // onClick listener needs to be defined here so we can access the payload and save expansion state
        holder.dataItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenInfo.getVisibility() == View.VISIBLE) {
                    hiddenInfo.setVisibility(View.GONE);
                    payload.setExpanded(false);
                    arrow.setRotation(0);

                    mExpandedPayloadIndex = -1;
                } else {
                    hiddenInfo.setVisibility(View.VISIBLE);
                    payload.setExpanded(true);
                    arrow.setRotation(90);

                    // Hide the last expanded payload
                    if (mExpandedPayloadIndex >= 0) {
                        PayloadAdapter.PayloadViewHolder viewHolder = (PayloadAdapter.PayloadViewHolder)mView.findViewHolderForAdapterPosition(
                                mExpandedPayloadIndex);

                        if (viewHolder != null) {
                            ConstraintLayout previousHiddenInfo = viewHolder.dataItem.findViewById(R.id.constraint_hidden_info);
                            previousHiddenInfo.setVisibility(View.GONE);

                            ImageView previousArrow = viewHolder.dataItem.findViewById(R.id.image_arrow);
                            previousArrow.setRotation(0);
                        }

                        mData.get(mExpandedPayloadIndex).setExpanded(false);
                    }

                    mExpandedPayloadIndex = position;
                }
            }
        });

        TextView time = holder.dataItem.findViewById(R.id.text_time);
        time.setText(String.format(mApplicationContext.getString(R.string.payload_time_profile_format), payload.getTime(),
                payload.getProfileType()));

        TextView content = holder.dataItem.findViewById(R.id.text_payload);
        String payloadString = payload.getPayload();
        content.setText(payloadString);

        TextView bytes = holder.dataItem.findViewById(R.id.text_bytes);
        String bytesString;

        if (payload.isValid()) {
            bytesString = mApplicationContext.getResources().getString(R.string.bytes) + " " + payload.getPayLoadLength();
            bytes.setText(bytesString);
            bytes.setVisibility(View.VISIBLE);
        } else {
            content.setTextColor(mApplicationContext.getResources().getColor(R.color.color_2));
            bytes.setVisibility(View.INVISIBLE); // Payload text view is being used for the string "failed", length is irrelevant
        }

        TextView profile = holder.dataItem.findViewById(R.id.text_profile);
        String profileType = payload.getProfileType();
        String profileString = mApplicationContext.getResources().getString(R.string.profile) + " " + profileType;
        profile.setText(profileString);

        if (payload.isExpanded()) {
            hiddenInfo.setVisibility(View.VISIBLE);
            arrow.setRotation(90);
        } else {
            hiddenInfo.setVisibility(View.GONE);
            arrow.setRotation(0);
        }

        // Only display signal quality/strength if we have the data for it
        ConstraintLayout pkabInfo = holder.dataItem.findViewById(R.id.constraint_pkab_info);

        if (!mRestrictedDisplayProfiles.contains(profileType.toLowerCase())) {
            pkabInfo.setVisibility(View.VISIBLE);

            double signalToNoiseRatioGreen;
            double snrDb = payload.getSignalStrength();
            TextView txtSnr = holder.dataItem.findViewById(R.id.text_signal_strength);
            txtSnr.setText(String.format(mApplicationContext.getString(R.string.db_format), snrDb));

            if (snrDb > 40.0) {
                signalToNoiseRatioGreen = 1.0;
            } else if (snrDb < 0.0) {
                signalToNoiseRatioGreen = 0.0;
            } else {
                signalToNoiseRatioGreen = (double) (snrDb / 40.0);
            }

            txtSnr.setBackgroundColor(HelperFunctions.colorGradient((int)(signalToNoiseRatioGreen * 100)));

            double headerQualityGreen;
            double headerEvmDb = payload.getHeaderQuality();
            TextView txtHeaderQuality = holder.dataItem.findViewById(R.id.text_header_quality);
            txtHeaderQuality.setText(String.format(mApplicationContext.getString(R.string.db_format), headerEvmDb));

            if (headerEvmDb < -24.0) {
                headerQualityGreen = 1.0;
            } else if (headerEvmDb > 0.0) {
                headerQualityGreen = 0.0;
            } else {
                headerQualityGreen = (double) (headerEvmDb / -24.0);
            }

            txtHeaderQuality.setBackgroundColor(HelperFunctions.colorGradient((int)(headerQualityGreen * 100)));

            TextView txtPayloadQuality = holder.dataItem.findViewById(R.id.text_payload_quality);

            if (payload.isValid()) {
                double payloadQualityGreen;
                double payloadEvmDb = payload.getPayloadQuality();
                txtPayloadQuality.setText(String.format(mApplicationContext.getString(R.string.db_format), payloadEvmDb));

                if (payloadEvmDb < -24.0) {
                    payloadQualityGreen = 1.0;
                } else if (payloadEvmDb > 0.0) {
                    payloadQualityGreen = 0.0;
                } else {
                    payloadQualityGreen = (double) (payloadEvmDb / -24.0);
                }

                txtPayloadQuality.setBackgroundColor(HelperFunctions.colorGradient((int)(payloadQualityGreen * 100)));
            } else {
                txtPayloadQuality.setText("");
                txtPayloadQuality.setBackgroundColor(mApplicationContext.getResources().getColor(R.color.color_2));
            }
        } else {
            pkabInfo.setVisibility(View.GONE);
        }
        System.out.println("LSNRJ Time que muestra: " + time.getText().toString() + "\n"+
                "LSNRJ content que muestra: " + content.getText().toString() + "\n" +
                "LSNRJ bytes que muestra: " + bytes.getText().toString() + "\n" );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Adds the string array to the data set
    public void add(Payload data) {
        mData.add(data);
        mView.smoothScrollToPosition(mData.size() - 1);
    }

    // Clears the local data set and updates the containing RecyclerView
    public void clearData() {
        mExpandedPayloadIndex = -1;
        mData.clear();
        mView.removeAllViews();
    }
}
