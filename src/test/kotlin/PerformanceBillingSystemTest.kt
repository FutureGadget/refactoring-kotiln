import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test

val INVOICES = loadResource("invoices.json")
val PLAYS = loadResource("plays.json")

internal class PerformanceBillingSystemTest {

    val format = Json { isLenient = true }

    @Test
    fun jsonParseTest() {
        val invoices = format.parseToJsonElement(INVOICES)
        val plays = format.parseToJsonElement(PLAYS)

        println(invoices)
        println(plays)

        println("playName: ${plays.jsonObject.getValue("hamlet").jsonObject.getValue("name").jsonPrimitive.content}")
        println("type: ${plays.jsonObject.getValue("hamlet").jsonObject.getValue("type").jsonPrimitive.content}")
    }
}
