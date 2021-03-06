package au.com.dius.pact.model.generators

import android.os.Build
import au.com.dius.pact.model.PactSpecVersion
import com.mifmif.common.regex.Generex
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberFunctions

fun lookupGenerator(generatorMap: Map<String, Any>): Generator? {
  var generator: Generator? = null

  try {
    val generatorClass = Class.forName("au.com.dius.au.com.dius.pact.model.generators.${generatorMap["type"]}Generator").kotlin
    val fromMap = generatorClass.companionObject?.declaredMemberFunctions?.find { it.name == "fromMap" }
    if (fromMap != null) {
      generator = fromMap.call(generatorClass.companionObjectInstance, generatorMap) as Generator?
    }
  } catch (e: ClassNotFoundException) { }

  return generator
}

interface Generator {
  fun generate(base: Any?): Any
  fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any>
}

data class RandomIntGenerator(val min: Int, val max: Int) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "RandomInt", "min" to min, "max" to max)
  }

  override fun generate(base: Any?): Any {
    return RandomUtils.nextInt(min, max)
  }

  companion object {
    fun fromMap(map: Map<String, Any>) : RandomIntGenerator {
      val min = if (map["min"] is Number) {
        (map["min"] as Number).toInt()
      } else {
        0
      }
      val max = if (map["max"] is Number) {
        (map["max"] as Number).toInt()
      } else {
        Int.MAX_VALUE
      }
      return RandomIntGenerator(min, max)
    }
  }
}

data class RandomDecimalGenerator(val digits: Int) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "RandomDecimal", "digits" to digits)
  }

  override fun generate(base: Any?): Any = BigDecimal(RandomStringUtils.randomNumeric(digits))

  companion object {
    fun fromMap(map: Map<String, Any>) : RandomDecimalGenerator {
      val digits = if (map["digits"] is Number) {
        (map["digits"] as Number).toInt()
      } else {
        10
      }
      return RandomDecimalGenerator(digits)
    }
  }
}

data class RandomHexadecimalGenerator(val digits: Int) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "RandomHexadecimal", "digits" to digits)
  }

  override fun generate(base: Any?): Any = RandomStringUtils.random(digits, "0123456789abcdef")

  companion object {
    fun fromMap(map: Map<String, Any>) : RandomHexadecimalGenerator {
      val digits = if (map["digits"] is Number) {
        (map["digits"] as Number).toInt()
      } else {
        10
      }
      return RandomHexadecimalGenerator(digits)
    }
  }
}

data class RandomStringGenerator(val size: Int = 20) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "RandomString", "size" to size)
  }

  override fun generate(base: Any?): Any {
    return RandomStringUtils.randomAlphanumeric(size)
  }

  companion object {
    fun fromMap(map: Map<String, Any>) : RandomStringGenerator {
      val size = if (map["size"] is Number) {
        (map["size"] as Number).toInt()
      } else {
        10
      }
      return RandomStringGenerator(size)
    }
  }
}

data class RegexGenerator(val regex: String) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "Regex", "regex" to regex)
  }

  override fun generate(base: Any?): Any = Generex(regex).random()

  companion object {
    fun fromMap(map: Map<String, Any>) = RegexGenerator(map["regex"]!! as String)
  }
}

class UuidGenerator : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "Uuid")
  }

  override fun generate(base: Any?): Any {
    return UUID.randomUUID().toString()
  }

  override fun equals(other: Any?) = other is UuidGenerator
  override fun hashCode() = super.hashCode()

  companion object {
    @Suppress("UNUSED_PARAMETER")
    fun fromMap(map: Map<String, Any>) : UuidGenerator {
      return UuidGenerator()
    }
  }
}

data class DateGenerator(val format: String? = null) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    if (format != null) {
      return mapOf("type" to "Date", "format" to this.format)
    }
    return mapOf("type" to "Date")
  }

  override fun generate(base: Any?): Any {
    return if (format != null) {
      DateTime.now().toString(DateTimeFormatterBuilder().appendPattern(format).toFormatter())
    } else {
      DateTime.now().toString()
    }
  }

  companion object {
    fun fromMap(map: Map<String, Any>) : DateGenerator {
      return DateGenerator(map["format"] as String?)
    }
  }

}

data class TimeGenerator(val format: String? = null) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    if (format != null) {
      return mapOf("type" to "Time", "format" to this.format)
    }
    return mapOf("type" to "Time")
  }

  override fun generate(base: Any?): Any {
    return if (format != null) {
      DateTime.now().toString(DateTimeFormatterBuilder().appendPattern(format).toFormatter())
    } else {
      DateTime.now().toString()
    }
  }

  companion object {
    fun fromMap(map: Map<String, Any>) : TimeGenerator {
      return TimeGenerator(map["format"] as String?)
    }
  }

}

data class DateTimeGenerator(val format: String? = null) : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    if (format != null) {
      return mapOf("type" to "DateTime", "format" to this.format)
    }
    return mapOf("type" to "DateTime")
  }

  override fun generate(base: Any?): Any {
    return if (format != null) {
      DateTime.now().toString(DateTimeFormatterBuilder().appendPattern(format).toFormatter())
    } else {
      DateTime.now().toString()
    }
  }

  companion object {
    fun fromMap(map: Map<String, Any>) : DateTimeGenerator {
      return DateTimeGenerator(map["format"] as String?)
    }
  }

}

object RandomBooleanGenerator : Generator {
  override fun toMap(pactSpecVersion: PactSpecVersion): Map<String, Any> {
    return mapOf("type" to "RandomBoolean")
  }

  override fun generate(base: Any?): Any {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ThreadLocalRandom.current().nextBoolean()
    } else {
      Random().nextBoolean()
    }
  }

  override fun equals(other: Any?) = other is RandomBooleanGenerator
  override fun hashCode() = super.hashCode()

  @Suppress("UNUSED_PARAMETER")
  fun fromMap(map: Map<String, Any>) : RandomBooleanGenerator {
    return RandomBooleanGenerator
  }
}
