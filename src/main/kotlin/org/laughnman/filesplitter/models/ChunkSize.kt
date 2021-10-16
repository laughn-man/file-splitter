package org.laughnman.filesplitter.models

import java.lang.RuntimeException

enum class ChunkSizeUnit(val multiplier: Long) {
	B(1), KB(1_000), MB(1_000_000), GB(1_000_000_000), TB(1_000_000_000_000)
}

fun String.toChunkSize(): ChunkSize {
	val regex = Regex("""^(\d+)((?:B|KB|MB|GB|TB)?)$""")

	return regex.matchEntire(this)
		?.let { ChunkSize(it.groupValues[1].toLong(), ChunkSizeUnit.valueOf(it.groupValues[2].takeUnless { it.isEmpty() } ?: "B")) }
		?: throw RuntimeException("$this did not match the format of ")
}

fun Long.toChunkSize(unit: ChunkSizeUnit = ChunkSizeUnit.B) = ChunkSize(this, unit)

fun Int.toChunkSize(unit: ChunkSizeUnit = ChunkSizeUnit.B) = this.toLong().toChunkSize(unit)

data class ChunkSize(val size: Long, val unit: ChunkSizeUnit = ChunkSizeUnit.B) {

	fun toBytes() = size * unit.multiplier

	fun toUnit(newUnit: ChunkSizeUnit) = ChunkSize(toBytes() / newUnit.multiplier, newUnit)

	override fun toString(): String {
		return "$size$unit"
	}
}