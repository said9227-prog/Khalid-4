package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KhalidStudioDatabase
import com.example.data.ProjectEntity
import com.example.data.ProjectRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var repository: ProjectRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = KhalidStudioDatabase.getDatabase(context)
        repository = ProjectRepository(db.projectDao())
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Khalid 3D", appName)
    }

    @Test
    fun `save and load project from room database`() = runBlocking {
        val newProject = ProjectEntity(
            title = "خالد السينمائي",
            isArabic = true,
            jsonContent = "{'text': 'خالد'}",
            durationSec = 5.0f
        )
        val savedId = repository.saveProject(newProject)
        val loadedProject = repository.getProjectById(savedId)

        assertNotNull(loadedProject)
        assertEquals("خالد السينمائي", loadedProject?.title)
        assertEquals(true, loadedProject?.isArabic)
    }
}
