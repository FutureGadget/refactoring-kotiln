import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.lang.Integer.max
import java.text.NumberFormat
import java.util.*
import kotlin.math.floor

class PerformanceBillingSystem {
    fun statement(invoice: JsonObject, plays: JsonObject): String {
        var totalAmount = 0
        var volumeCredits = 0
        var result = "청구 내역 (고객명: ${invoice.getValue("customer").jsonPrimitive.content})\n"
        val format = NumberFormat.getCurrencyInstance().apply {
            minimumFractionDigits = 2
            currency = Currency.getInstance("USD")
        }

        for (perf in invoice["performances"]!!.jsonArray.iterator()) {
            val play = plays[perf.get<String>("playID")]!!
            var thisAmount = 0

            when (play.get<String>("type")) {
                "tragedy" -> {
                    thisAmount = 40000
                    if (perf.get<Int>("audience") > 30) {
                        thisAmount += 1000 * (perf.get<Int>("audience") - 30)
                    }
                }
                "comedy" -> {
                    thisAmount = 30000
                    if (perf.get<Int>("audience") > 20) {
                        thisAmount += 10000 + 500 * (perf.get<Int>("audience") - 20)
                    }
                    thisAmount += 300 * perf.get<Int>("audience")
                }
                else -> throw IllegalArgumentException("알 수 없는 장르: ${play.get<String>("type")}")
            }
            volumeCredits += max(perf.get<Int>("audience") - 30, 0)
            if ("comedy" == play.get<String>("type")) volumeCredits += floor(perf.get<Float>("audience") / 5).toInt()

            result += " ${play.get<String>("name")}: ${format.format(thisAmount / 100.0)} (${perf.get<Int>("audience")}석)\n"
            totalAmount += thisAmount
        }
        result += "총액: ${format.format(totalAmount / 100.0)}\n"
        result += "적립 포인트: ${volumeCredits}점\n"
        return result
    }
}

fun main() {
    val INVOICES = loadResource("invoices.json")
    val PLAYS = loadResource("plays.json")
    println(
        PerformanceBillingSystem().statement(
            Json.parseToJsonElement(INVOICES).jsonArray.first().jsonObject,
            Json.parseToJsonElement(PLAYS).jsonObject
        )
    )
}

fun JsonElement.get(key: String): JsonPrimitive {
    return this.jsonObject.getValue(key).jsonPrimitive
}

inline fun <reified T> JsonElement.get(key: String): T {
    val primitive = this.jsonObject.getValue(key).jsonPrimitive
    return when(T::class) {
        Int::class -> primitive.int as T
        Float::class -> primitive.float as T
        String::class -> primitive.content as T
        else -> throw IllegalArgumentException("Unsupported Type ${T::class}")
    }
}

fun loadResource(file: String) = {}::class.java.getResource(file).readText()