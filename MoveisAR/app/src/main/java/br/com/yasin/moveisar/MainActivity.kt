package br.com.yasin.moveisar

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var selectedObject: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeGallery()

        (fragment_sceneform as ArFragment).setOnTapArPlaneListener { hitResult, plane, _ ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }

            val anchor = hitResult.createAnchor()

            placeObject(fragment_sceneform as ArFragment, anchor, selectedObject)
        }
    }


    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri?) {
        ModelRenderable.builder()
            .setSource(fragment.context!!, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(fragment, anchor, renderable) }
            .exceptionally { throwable ->
                Toast.makeText(fragment.context, throwable.message, Toast.LENGTH_SHORT).show()
                null
            }

    }

    private fun initializeGallery() {
        linear_gallery.addView(
            createImageView(
                R.drawable.chair_icon,
                "chair",
                "chair.sfb"
            )
        )
    }

    private fun createImageView(drawable: Int, description: String, uriString: String): ImageView {
        val img = ImageView(this)
        img.setImageResource(drawable)
        img.contentDescription = description
        img.setOnClickListener { selectedObject = Uri.parse(uriString) }
        return img
    }
}
