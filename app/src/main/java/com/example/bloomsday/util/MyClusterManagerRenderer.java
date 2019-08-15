package com.example.bloomsday.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.bloomsday.R;
import com.example.bloomsday.models.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;

import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public MyClusterManagerRenderer(Context context, GoogleMap googleMap,
                                    ClusterManager<ClusterMarker> clusterManager) {

        super( context, googleMap, clusterManager );

        // initialize cluster item icon generator
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension( R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight)); //LayoutParams are used by views to tell their parents how they want to be laid out.
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

    }

    /**
     * Rendering of the individual ClusterItems
     *
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        imageView.setImageResource( item.getIconPicture() );
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon( BitmapDescriptorFactory.fromBitmap( icon ) ).title( item.getTitle() );
        //BitmapDescriptionFactory is used to create a definition of a Bitmap image, used for marker icons and ground overlays.
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }

    /**
     * Update the GPS coordinate of a ClusterItem
     *
     * @param clusterMarker
     */
    public void setUpdateMarker(ClusterMarker clusterMarker) {
        Marker marker = getMarker( clusterMarker );
        if (marker != null) {
            marker.setPosition( clusterMarker.getPosition() );
        }
    }


}