package pro.shineapp.api.di

import com.google.common.truth.Truth.assertThat
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.org.webcompere.systemstubs.rules.EnvironmentVariablesRule

class AppComponentKtTest {

    private lateinit var mockConfig: ApplicationConfig
    private lateinit var configValue: ApplicationConfigValue

    @get:Rule
    val environmentVariablesRule = EnvironmentVariablesRule()

    @Before
    fun setUp() {
        mockConfig = mockk()
        environmentVariablesRule.set("JWT_SECRET", "some secret")
        configValue = mockk(relaxed = true)

        every { configValue.getList() } returns emptyList()
        every { mockConfig.propertyOrNull(any()) } returns configValue
        every { mockConfig.property(any()) } returns configValue

    }
    @Test
    fun `the app component instance always the same`() = testApplication {
        environment {
            config = mockConfig
        }

        application {
            val comp1 = appComponent
            val comp2 = appComponent
            val comp3 = appComponent
            assertThat(comp1).isSameInstanceAs(comp2)
            assertThat(comp1).isSameInstanceAs(comp3)
        }
    }
}