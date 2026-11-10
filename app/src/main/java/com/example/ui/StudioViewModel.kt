package com.example.ui

import android.app.Application
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KhalidStudioDatabase
import com.example.data.ProjectEntity
import com.example.data.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class StudioTab {
    TEXT,
    TERRAIN,
    SURFACE_PAINT,
    MATERIALS,
    LIGHTING,
    CAMERA,
    ANIMATION,
    AUDIO,
    EXPORT,
    PROJECTS
}

data class CameraMotionStep(
    val id: String = "step_" + System.currentTimeMillis() + "_" + (100..999).random(),
    val preset: String,
    val durationSec: Float
)

enum class SceneAssetType {
    TEXT,
    IMAGE,
    MODEL
}

data class SceneAssetItem(
    val id: String,
    val type: SceneAssetType,
    var label: String,
    var text: String = "",
    var imageBase64: String = "",
    var modelType: String = "cube", // cube, sphere, cylinder, cone, torus, star, pyramid, ring
    var posX: Float = 0f,
    var posY: Float = 0f,
    var posZ: Float = 0f,
    var rotX: Float = 0f,
    var rotY: Float = 0f,
    var rotZ: Float = 0f,
    var scaleX: Float = 1.0f,
    var scaleY: Float = 1.0f,
    var scaleZ: Float = 1.0f,
    var size: Float = 1.4f,
    var depth: Float = 0.5f,
    var bevel: Float = 0.08f,
    var spacing: Float = 1.25f,
    var materialPreset: String = "gold"
)

data class TextObjectItem(
    val id: String,
    var label: String,
    var text: String,
    var size: Float = 1.4f,
    var depth: Float = 0.5f,
    var bevel: Float = 0.08f,
    var spacing: Float = 1.25f,
    var posX: Float = 0f,
    var posY: Float = 0f,
    var posZ: Float = 0f,
    var materialPreset: String = "gold"
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    val projectsList: StateFlow<List<ProjectEntity>>

    var webViewRef: WebView? = null

    val isArabic = MutableStateFlow(false)
    val activeTab = MutableStateFlow(StudioTab.TEXT)

    // Multi-Asset 3D Scene Manager (Texts, 3D Image Planes, & 3D Primitive Models)
    val isText3DAnimating = MutableStateFlow(true)

    val sceneAssetsList = MutableStateFlow<List<SceneAssetItem>>(
        listOf(
            SceneAssetItem(
                id = "text_main",
                type = SceneAssetType.TEXT,
                label = "النص الرئيسي",
                text = "KHALID 3D",
                posY = 0.5f
            )
        )
    )

    val selectedAssetId = MutableStateFlow("text_main")

    // Legacy State compatibility
    val textObjectsList = MutableStateFlow(
        listOf(
            TextObjectItem("text_main", "النص 1", "KHALID 3D", posY = 0.5f)
        )
    )
    val selectedTextObjectId = MutableStateFlow("text_main")

    // Selected Asset Active Parameters
    val currentText = MutableStateFlow("KHALID 3D")
    val textSize = MutableStateFlow(1.4f)
    val textDepth = MutableStateFlow(0.5f)
    val bevelDepth = MutableStateFlow(0.08f)
    val letterSpacing = MutableStateFlow(1.25f)

    // 3D Transform Parameters (X, Y, Z Position, Rotation in degrees, and Scale)
    val assetPosX = MutableStateFlow(0.0f)
    val assetPosY = MutableStateFlow(0.5f)
    val assetPosZ = MutableStateFlow(0.0f)

    val assetRotX = MutableStateFlow(0.0f)
    val assetRotY = MutableStateFlow(0.0f)
    val assetRotZ = MutableStateFlow(0.0f)

    val assetScaleX = MutableStateFlow(1.0f)
    val assetScaleY = MutableStateFlow(1.0f)
    val assetScaleZ = MutableStateFlow(1.0f)

    // Legacy position bindings
    val textPosX = assetPosX
    val textPosY = assetPosY
    val textPosZ = assetPosZ

    // Material state
    val selectedMaterial = MutableStateFlow("gold")
    val metalness = MutableStateFlow(0.9f)
    val roughness = MutableStateFlow(0.18f)
    val customColorHex = MutableStateFlow("#FFD700")
    val customEmissive = MutableStateFlow(0.2f)

    // Interactive 3D Light Bulb & Lighting Controls
    val lightPosX = MutableStateFlow(0.0f)
    val lightPosY = MutableStateFlow(4.0f)
    val lightPosZ = MutableStateFlow(3.5f)
    val lightIntensity = MutableStateFlow(3.5f)
    val lightColorHex = MutableStateFlow("#FFD700")
    val isLightBulbVisible = MutableStateFlow(true)
    val isCameraPathVisible = MutableStateFlow(true)

    // 3D Surface Painting Studio (3ds Max Object Paint & Scatter Engine)
    val isPaintModeEnabled = MutableStateFlow(false)
    val activeBrushType = MutableStateFlow("grass") // "grass", "trees", "flowers", "rocks", "stars"
    val activeBrushTool = MutableStateFlow("paint") // "paint", "erase"
    val brushRadius = MutableStateFlow(2.5f)
    val brushDensity = MutableStateFlow(8)
    val totalPaintedCount = MutableStateFlow(0)

    // Environment, Ground, Backdrop & Procedural 3D Terrain Studio
    val stageTheme = MutableStateFlow("3dsmax_white")
    val groundPreset = MutableStateFlow("grass_meadow")
    val backdropPreset = MutableStateFlow("mountains")
    val renderQuality = MutableStateFlow("balanced")
    val selectedLightPreset = MutableStateFlow("studio_gold")
    val isBloomEnabled = MutableStateFlow(true)
    val isGridVisible = MutableStateFlow(true)
    val isTextureAnimating = MutableStateFlow(false)

    // 3D Procedural Terrain Studio State
    val terrainPreset = MutableStateFlow("grass_meadow")
    val terrainDisplacementHeight = MutableStateFlow(1.0f)
    val terrainNoiseFreq = MutableStateFlow(1.0f)
    val waterWaveSpeed = MutableStateFlow(1.0f)
    val terrainQualityTier = MutableStateFlow("balanced")

    // Space & Sky Celestial Environment Engine (نجوم تلمع، قمر كامل/هلال، شمس وغيوم)
    val skyPreset = MutableStateFlow("deep_space")
    val moonPhase = MutableStateFlow("full_moon")
    val starDensity = MutableStateFlow(1000)
    val cloudSpeed = MutableStateFlow(0.05f)
    val starTwinkleSpeed = MutableStateFlow(1.0f)

    // Camera Studio & Multi-Shot Sequence Choreographer
    val isRecordingCamera = MutableStateFlow(false)
    val recordedCameraFrames = MutableStateFlow(0)
    val selectedCameraPreset = MutableStateFlow("orbit360")
    val cameraRecDurationSec = MutableStateFlow(5.0f)
    val cameraSequence = MutableStateFlow(
        listOf(
            CameraMotionStep(preset = "top", durationSec = 4.0f),
            CameraMotionStep(preset = "bottom", durationSec = 4.0f),
            CameraMotionStep(preset = "orbit360", durationSec = 5.0f)
        )
    )

    // Timeline & Animation
    val isPlaying = MutableStateFlow(false)
    val currentTime = MutableStateFlow(0.0f)
    val durationSec = MutableStateFlow(5.0f)
    val fps = MutableStateFlow(30)

    // Audio
    val selectedAudioTrackName = MutableStateFlow<String?>(null)
    val audioVolume = MutableStateFlow(0.8f)

    // Export State
    val isExporting = MutableStateFlow(false)
    val exportProgress = MutableStateFlow(0)
    val exportedFilePath = MutableStateFlow<String?>(null)
    val exportFormat = MutableStateFlow("mp4")
    val exportResolution = MutableStateFlow("1080p")

    init {
        val dao = KhalidStudioDatabase.getDatabase(application).projectDao()
        repository = ProjectRepository(dao)
        projectsList = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun toggleLanguage() {
        isArabic.value = !isArabic.value
    }

    fun toggleText3DAnimation(enabled: Boolean) {
        isText3DAnimating.value = enabled
        val js = "window.studioEngine && window.studioEngine.toggleText3DAnimation($enabled);"
        evaluateJs(js)
    }

    fun selectAsset(id: String) {
        selectedAssetId.value = id
        selectedTextObjectId.value = id

        val asset = sceneAssetsList.value.find { it.id == id } ?: return
        currentText.value = asset.text
        textSize.value = asset.size
        textDepth.value = asset.depth
        bevelDepth.value = asset.bevel
        letterSpacing.value = asset.spacing

        assetPosX.value = asset.posX
        assetPosY.value = asset.posY
        assetPosZ.value = asset.posZ

        assetRotX.value = asset.rotX
        assetRotY.value = asset.rotY
        assetRotZ.value = asset.rotZ

        assetScaleX.value = asset.scaleX
        assetScaleY.value = asset.scaleY
        assetScaleZ.value = asset.scaleZ
    }

    fun selectTextObject(id: String) {
        selectAsset(id)
    }

    fun addNewTextAsset(text: String = "نص جديد 3D") {
        val count = sceneAssetsList.value.filter { it.type == SceneAssetType.TEXT }.size + 1
        val newId = "text_" + System.currentTimeMillis()
        val defaultY = if (count % 2 == 0) -1.2f * (count / 2) else 1.2f * (count / 2)
        val newItem = SceneAssetItem(
            id = newId,
            type = SceneAssetType.TEXT,
            label = "نص $count",
            text = text,
            posY = defaultY
        )

        sceneAssetsList.value = sceneAssetsList.value + newItem
        syncLegacyTextList()
        selectAsset(newId)

        val js = "window.studioEngine && window.studioEngine.addTextObject({ id: '$newId', text: '${escapeJs(text)}', size: 1.2, depth: 0.4, posY: $defaultY, posX: 0, posZ: 0, materialPreset: 'gold' });"
        evaluateJs(js)
    }

    fun addNewTextObject(text: String = "نص جديد 3D") {
        addNewTextAsset(text)
    }

    fun addNewImageAsset(base64Data: String = "", label: String = "صورة 3D") {
        val count = sceneAssetsList.value.filter { it.type == SceneAssetType.IMAGE }.size + 1
        val newId = "img_" + System.currentTimeMillis()
        val defaultY = 0.0f
        val defaultZ = -0.5f
        val displayLabel = if (label.isNotBlank()) label else "صورة $count"
        val newItem = SceneAssetItem(
            id = newId,
            type = SceneAssetType.IMAGE,
            label = displayLabel,
            imageBase64 = base64Data,
            posY = defaultY,
            posZ = defaultZ
        )

        sceneAssetsList.value = sceneAssetsList.value + newItem
        selectAsset(newId)

        val js = if (base64Data.isNotBlank()) {
            "window.studioEngine && window.studioEngine.addImageObject({ id: '$newId', label: '${escapeJs(displayLabel)}', base64Url: '$base64Data', posY: $defaultY, posZ: $defaultZ });"
        } else {
            "window.studioEngine && window.studioEngine.addImageObject({ id: '$newId', label: '${escapeJs(displayLabel)}', posY: $defaultY, posZ: $defaultZ });"
        }
        evaluateJs(js)
    }

    fun addNewModelAsset(modelType: String = "cube", label: String = "مجسم 3D") {
        val count = sceneAssetsList.value.filter { it.type == SceneAssetType.MODEL }.size + 1
        val newId = "model_" + System.currentTimeMillis()
        val defaultX = if (count % 2 == 0) -2.0f else 2.0f
        val displayLabel = "$label $count"
        val newItem = SceneAssetItem(
            id = newId,
            type = SceneAssetType.MODEL,
            label = displayLabel,
            modelType = modelType,
            posX = defaultX,
            posY = 0.0f,
            materialPreset = selectedMaterial.value
        )

        sceneAssetsList.value = sceneAssetsList.value + newItem
        selectAsset(newId)

        val js = "window.studioEngine && window.studioEngine.addModelObject({ id: '$newId', modelType: '$modelType', posX: $defaultX, posY: 0, posZ: 0, materialPreset: '${selectedMaterial.value}' });"
        evaluateJs(js)
    }

    fun duplicateSelectedAsset() {
        val currentId = selectedAssetId.value
        val item = sceneAssetsList.value.find { it.id == currentId } ?: return
        val count = sceneAssetsList.value.size + 1
        val newId = "${item.type.name.lowercase()}_" + System.currentTimeMillis()
        val newY = item.posY - 1.0f
        val newItem = item.copy(id = newId, label = "نسخة $count", posY = newY)

        sceneAssetsList.value = sceneAssetsList.value + newItem
        syncLegacyTextList()
        selectAsset(newId)

        when (item.type) {
            SceneAssetType.TEXT -> {
                val js = "window.studioEngine && window.studioEngine.addTextObject({ id: '$newId', text: '${escapeJs(newItem.text)}', size: ${newItem.size}, depth: ${newItem.depth}, posY: $newY, posX: ${newItem.posX}, posZ: ${newItem.posZ}, materialPreset: '${newItem.materialPreset}' });"
                evaluateJs(js)
            }
            SceneAssetType.IMAGE -> {
                val js = "window.studioEngine && window.studioEngine.addImageObject({ id: '$newId', label: '${escapeJs(newItem.label)}', base64Url: '${newItem.imageBase64}', posY: $newY, posX: ${newItem.posX}, posZ: ${newItem.posZ} });"
                evaluateJs(js)
            }
            SceneAssetType.MODEL -> {
                val js = "window.studioEngine && window.studioEngine.addModelObject({ id: '$newId', modelType: '${newItem.modelType}', posY: $newY, posX: ${newItem.posX}, posZ: ${newItem.posZ}, materialPreset: '${newItem.materialPreset}' });"
                evaluateJs(js)
            }
        }
    }

    fun duplicateSelectedTextObject() {
        duplicateSelectedAsset()
    }

    fun deleteSelectedAsset() {
        if (sceneAssetsList.value.size <= 1) return
        val currentId = selectedAssetId.value
        val newList = sceneAssetsList.value.filter { it.id != currentId }
        sceneAssetsList.value = newList
        syncLegacyTextList()

        val nextItem = newList.first()
        selectAsset(nextItem.id)

        val js = "window.studioEngine && window.studioEngine.removeObject('$currentId');"
        evaluateJs(js)
    }

    fun deleteSelectedTextObject() {
        deleteSelectedAsset()
    }

    fun updateSelectedAssetTransform(
        posX: Float = assetPosX.value,
        posY: Float = assetPosY.value,
        posZ: Float = assetPosZ.value,
        rotX: Float = assetRotX.value,
        rotY: Float = assetRotY.value,
        rotZ: Float = assetRotZ.value,
        scaleX: Float = assetScaleX.value,
        scaleY: Float = assetScaleY.value,
        scaleZ: Float = assetScaleZ.value
    ) {
        assetPosX.value = posX
        assetPosY.value = posY
        assetPosZ.value = posZ

        assetRotX.value = rotX
        assetRotY.value = rotY
        assetRotZ.value = rotZ

        assetScaleX.value = scaleX
        assetScaleY.value = scaleY
        assetScaleZ.value = scaleZ

        val activeId = selectedAssetId.value
        sceneAssetsList.value.find { it.id == activeId }?.let { item ->
            item.posX = posX
            item.posY = posY
            item.posZ = posZ

            item.rotX = rotX
            item.rotY = rotY
            item.rotZ = rotZ

            item.scaleX = scaleX
            item.scaleY = scaleY
            item.scaleZ = scaleZ
        }
        syncLegacyTextList()

        val js = "window.studioEngine && window.studioEngine.updateObjectTransform('$activeId', { posX: $posX, posY: $posY, posZ: $posZ, rotX: $rotX, rotY: $rotY, rotZ: $rotZ, scaleX: $scaleX, scaleY: $scaleY, scaleZ: $scaleZ });"
        evaluateJs(js)
    }

    fun updateSelectedTextPosition(x: Float = assetPosX.value, y: Float = assetPosY.value, z: Float = assetPosZ.value) {
        updateSelectedAssetTransform(posX = x, posY = y, posZ = z)
    }

    private fun syncLegacyTextList() {
        textObjectsList.value = sceneAssetsList.value
            .filter { it.type == SceneAssetType.TEXT }
            .map {
                TextObjectItem(
                    id = it.id,
                    label = it.label,
                    text = it.text,
                    size = it.size,
                    depth = it.depth,
                    bevel = it.bevel,
                    spacing = it.spacing,
                    posX = it.posX,
                    posY = it.posY,
                    posZ = it.posZ,
                    materialPreset = it.materialPreset
                )
            }
    }

    fun updateTextConfig(
        text: String = currentText.value,
        size: Float = textSize.value,
        depth: Float = textDepth.value,
        bevel: Float = bevelDepth.value,
        spacing: Float = letterSpacing.value
    ) {
        currentText.value = text
        textSize.value = size
        textDepth.value = depth
        bevelDepth.value = bevel
        letterSpacing.value = spacing

        val activeId = selectedAssetId.value
        sceneAssetsList.value.find { it.id == activeId }?.let { item ->
            item.text = text
            item.size = size
            item.depth = depth
            item.bevel = bevel
            item.spacing = spacing
        }
        syncLegacyTextList()

        val js = "window.studioEngine && window.studioEngine.updateTextObject('$activeId', { text: '${escapeJs(text)}', size: $size, depth: $depth, bevel: $bevel, letterSpacing: $spacing });"
        evaluateJs(js)
    }

    fun loadCustomTextureFromPhone(targetType: String, base64Url: String) {
        val js = "window.studioEngine && window.studioEngine.loadCustomTexture('$targetType', '$base64Url');"
        evaluateJs(js)
    }

    fun toggleTextureAnimation(enabled: Boolean) {
        isTextureAnimating.value = enabled
        val js = "window.studioEngine && window.studioEngine.toggleTextureAnimation($enabled);"
        evaluateJs(js)
    }

    fun setGroundTexture(preset: String) {
        groundPreset.value = preset
        val js = "window.studioEngine && window.studioEngine.setGroundTexture('$preset');"
        evaluateJs(js)
    }

    fun setTerrainPreset(preset: String) {
        terrainPreset.value = preset
        groundPreset.value = preset
        val js = "window.studioEngine && window.studioEngine.setTerrainPreset('$preset');"
        evaluateJs(js)
    }

    fun setTerrainDisplacementHeight(height: Float) {
        terrainDisplacementHeight.value = height
        val js = "window.studioEngine && window.studioEngine.setTerrainDisplacementHeight($height);"
        evaluateJs(js)
    }

    fun setTerrainNoiseFrequency(freq: Float) {
        terrainNoiseFreq.value = freq
        val js = "window.studioEngine && window.studioEngine.setTerrainNoiseFrequency($freq);"
        evaluateJs(js)
    }

    fun setWaterWaveSpeed(speed: Float) {
        waterWaveSpeed.value = speed
        val js = "window.studioEngine && window.studioEngine.setWaterWaveSpeed($speed);"
        evaluateJs(js)
    }

    fun setTerrainQualityTier(tier: String) {
        terrainQualityTier.value = tier
        setRenderQuality(tier)
    }

    fun setSkyPreset(preset: String) {
        skyPreset.value = preset
        val js = "window.studioEngine && window.studioEngine.setSkyPreset('$preset');"
        evaluateJs(js)
    }

    fun setMoonPhase(phase: String) {
        moonPhase.value = phase
        val js = "window.studioEngine && window.studioEngine.setMoonPhase('$phase');"
        evaluateJs(js)
    }

    fun setStarDensity(density: Int) {
        starDensity.value = density
        val js = "window.studioEngine && window.studioEngine.setStarDensity($density);"
        evaluateJs(js)
    }

    fun setCloudSpeed(speed: Float) {
        cloudSpeed.value = speed
        val js = "window.studioEngine && window.studioEngine.setCloudSpeed($speed);"
        evaluateJs(js)
    }

    fun setStarTwinkleSpeed(speed: Float) {
        starTwinkleSpeed.value = speed
        val js = "window.studioEngine && window.studioEngine.setStarTwinkleSpeed($speed);"
        evaluateJs(js)
    }

    fun setBackdropTexture(preset: String) {
        backdropPreset.value = preset
        val js = "window.studioEngine && window.studioEngine.setBackdropTexture('$preset');"
        evaluateJs(js)
    }

    fun setStageTheme(theme: String) {
        stageTheme.value = theme
        val js = "window.studioEngine && window.studioEngine.setStageTheme('$theme');"
        evaluateJs(js)
    }

    fun setRenderQuality(quality: String) {
        renderQuality.value = quality
        val js = "window.studioEngine && window.studioEngine.setRenderQuality('$quality');"
        evaluateJs(js)
    }

    fun setMaterialPreset(preset: String) {
        selectedMaterial.value = preset
        val js = "window.studioEngine && window.studioEngine.setMaterialPreset('text_main', '$preset');"
        evaluateJs(js)
    }

    fun setCustomMaterialColor(
        hexColor: String = customColorHex.value,
        metal: Float = metalness.value,
        rough: Float = roughness.value,
        emissive: Float = customEmissive.value
    ) {
        customColorHex.value = hexColor
        metalness.value = metal
        roughness.value = rough
        customEmissive.value = emissive
        selectedMaterial.value = "custom"

        val js = "window.studioEngine && window.studioEngine.setCustomMaterialColor('text_main', '$hexColor', $metal, $rough, $emissive);"
        evaluateJs(js)
    }

    fun updateLightPosition(x: Float = lightPosX.value, y: Float = lightPosY.value, z: Float = lightPosZ.value) {
        lightPosX.value = x
        lightPosY.value = y
        lightPosZ.value = z
        val js = "window.studioEngine && window.studioEngine.setLightPosition($x, $y, $z);"
        evaluateJs(js)
    }

    fun updateLightColor(hexColor: String) {
        lightColorHex.value = hexColor
        val js = "window.studioEngine && window.studioEngine.setLightColor('$hexColor');"
        evaluateJs(js)
    }

    fun updateLightIntensity(intensity: Float) {
        lightIntensity.value = intensity
        val js = "window.studioEngine && window.studioEngine.setLightIntensity($intensity);"
        evaluateJs(js)
    }

    fun toggleLightBulbMesh(visible: Boolean) {
        isLightBulbVisible.value = visible
        val js = "window.studioEngine && window.studioEngine.toggleLightBulbMesh($visible);"
        evaluateJs(js)
    }

    fun toggleCameraPathCurve(visible: Boolean) {
        isCameraPathVisible.value = visible
        if (visible) {
            updateCameraPathInEngine()
        } else {
            evaluateJs("window.studioEngine && window.studioEngine.updateCameraPathCurve('[]');")
        }
    }

    fun updateCameraPathInEngine() {
        if (!isCameraPathVisible.value) return
        val sequenceJson = serializeSequenceJson(cameraSequence.value)
        val js = "window.studioEngine && window.studioEngine.updateCameraPathCurve($sequenceJson);"
        evaluateJs(js)
    }

    fun togglePaintMode(enabled: Boolean) {
        isPaintModeEnabled.value = enabled
        val js = "window.studioEngine && window.studioEngine.togglePaintMode($enabled);"
        evaluateJs(js)
    }

    fun setBrushType(type: String) {
        activeBrushType.value = type
        val js = "window.studioEngine && window.studioEngine.setBrushType('$type');"
        evaluateJs(js)
    }

    fun setBrushTool(tool: String) {
        activeBrushTool.value = tool
        val js = "window.studioEngine && window.studioEngine.setBrushTool('$tool');"
        evaluateJs(js)
    }

    fun setBrushRadius(radius: Float) {
        brushRadius.value = radius
        val js = "window.studioEngine && window.studioEngine.setBrushRadius($radius);"
        evaluateJs(js)
    }

    fun setBrushDensity(density: Int) {
        brushDensity.value = density
        val js = "window.studioEngine && window.studioEngine.setBrushDensity($density);"
        evaluateJs(js)
    }

    fun scatterSelectedBrush(count: Int) {
        val type = activeBrushType.value
        val js = "window.studioEngine && window.studioEngine.scatterInstances('$type', $count);"
        evaluateJs(js)
    }

    fun clearPaintedSurface() {
        totalPaintedCount.value = 0
        val js = "window.studioEngine && window.studioEngine.clearAllPaintedInstances();"
        evaluateJs(js)
    }

    fun applyCameraPreset(preset: String) {
        selectedCameraPreset.value = preset
        val js = "window.studioEngine && window.studioEngine.applyCameraPreset('$preset');"
        evaluateJs(js)
    }

    fun addCameraSequenceStep(preset: String, durationSec: Float) {
        val newList = cameraSequence.value + CameraMotionStep(preset = preset, durationSec = durationSec)
        cameraSequence.value = newList
        updateCameraPathInEngine()
    }

    fun removeCameraSequenceStep(id: String) {
        cameraSequence.value = cameraSequence.value.filter { it.id != id }
        updateCameraPathInEngine()
    }

    fun previewCameraSequence() {
        val sequenceJson = serializeSequenceJson(cameraSequence.value)
        val js = "window.studioEngine && window.studioEngine.playCameraSequence($sequenceJson, false);"
        evaluateJs(js)
    }

    fun recordCameraSequence() {
        isRecordingCamera.value = true
        recordedCameraFrames.value = 0
        val sequenceJson = serializeSequenceJson(cameraSequence.value)
        val js = "window.studioEngine && window.studioEngine.playCameraSequence($sequenceJson, true);"
        evaluateJs(js)
    }

    private fun serializeSequenceJson(steps: List<CameraMotionStep>): String {
        val array = JSONArray()
        steps.forEach { step ->
            val obj = JSONObject()
            obj.put("preset", step.preset)
            obj.put("duration", step.durationSec)
            array.put(obj)
        }
        return array.toString()
    }

    fun startCameraRecording() {
        isRecordingCamera.value = true
        recordedCameraFrames.value = 0
        evaluateJs("window.studioEngine && window.studioEngine.startCameraRecording();")
    }

    fun stopCameraRecording() {
        isRecordingCamera.value = false
        evaluateJs("window.studioEngine && window.studioEngine.stopCameraRecording();")
    }

    fun replayCameraPath() {
        evaluateJs("window.studioEngine && window.studioEngine.replayCameraPath();")
    }

    fun togglePlayPause() {
        isPlaying.value = !isPlaying.value
        val state = if (isPlaying.value) "true" else "false"
        evaluateJs("if(window.studioEngine) window.studioEngine.isPlaying = $state;")
    }

    fun seekTimeline(time: Float) {
        currentTime.value = time
        evaluateJs("if(window.studioEngine) window.studioEngine.currentTime = $time;")
    }

    fun startVideoExport() {
        isExporting.value = true
        exportProgress.value = 0
        exportedFilePath.value = null
        val duration = durationSec.value
        val fpsVal = fps.value
        val fmt = exportFormat.value

        val js = "window.studioEngine && window.studioEngine.exportVideo($duration, $fpsVal, '$fmt');"
        evaluateJs(js)
    }

    fun onExportProgress(progress: Int) {
        exportProgress.value = progress
    }

    fun onExportComplete(savedPath: String) {
        isExporting.value = false
        exportedFilePath.value = savedPath
    }

    fun onCameraRecordingState(status: String, count: Int) {
        if (status == "stopped") {
            isRecordingCamera.value = false
            recordedCameraFrames.value = count
        }
    }

    fun saveCurrentProject(title: String) {
        viewModelScope.launch {
            val project = ProjectEntity(
                title = title.ifBlank { "Cinematic Text" },
                isArabic = isArabic.value,
                jsonContent = "{}",
                durationSec = durationSec.value,
                fps = fps.value
            )
            repository.saveProject(project)
        }
    }

    fun loadProject(project: ProjectEntity) {
        viewModelScope.launch {
            currentText.value = project.title
            durationSec.value = project.durationSec
            fps.value = project.fps
            updateTextConfig(text = project.title)
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    private fun evaluateJs(code: String) {
        webViewRef?.post {
            webViewRef?.evaluateJavascript(code, null)
        }
    }

    private fun escapeJs(str: String): String {
        return str.replace("'", "\\'").replace("\n", "\\n")
    }
}
