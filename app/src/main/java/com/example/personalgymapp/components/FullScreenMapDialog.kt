package com.example.personalgymapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.personalgymapp.R
import com.example.personalgymapp.model.GPSPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMapDialog(points: List<GPSPoint>, onDismiss: () -> Unit) {
    val pathPoints = points.map { GeoPoint(it.latitude, it.longitude) }
    var mapReference by remember { mutableStateOf<MapView?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.route_detail)) },
                    actions = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            setBuiltInZoomControls(false)
                            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                            if (pathPoints.isNotEmpty()) {
                                controller.setZoom(17.0)
                                controller.setCenter(pathPoints.first())
                                
                                val polyline = Polyline(this)
                                polyline.setPoints(pathPoints)
                                polyline.outlinePaint.color = android.graphics.Color.BLUE
                                polyline.outlinePaint.strokeWidth = 10f
                                overlays.add(polyline)
                            }
                            mapReference = this
                        }
                    },
                    update = { it.invalidate() },
                    modifier = Modifier.fillMaxSize(),
                    onRelease = { it.onDetach() }
                )

                // Safe-Positioned Zoom Controls
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .navigationBarsPadding(),
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { mapReference?.controller?.zoomIn() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Zoom In",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.width(24.dp),
                            thickness = 1.dp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                        IconButton(
                            onClick = { mapReference?.controller?.zoomOut() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Zoom Out",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
