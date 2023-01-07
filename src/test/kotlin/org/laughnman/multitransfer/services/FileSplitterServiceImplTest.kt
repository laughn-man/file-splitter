package org.laughnman.multitransfer.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeUnit
import org.laughnman.multitransfer.models.CombineCommand
import org.laughnman.multitransfer.models.SplitCommand
import org.laughnman.multitransfer.utilities.exceptions.InvalidFileSpecificationsException
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import kotlin.random.Random

@ExperimentalKotest
class FileSplitterServiceImplTest : DescribeSpec({

	val fileDao = mockk<FileDao>()
	val fileSplitterServiceImpl = FileSplitterServiceImpl(fileDao)

	afterTest {
		clearMocks(fileDao)
	}

	describe("splitFiles") {

		it("path is not a file") {
			val splitCommand = SplitCommand()
			splitCommand.path = Paths.get("test")

			every { fileDao.isFile(splitCommand.path.toFile()) } returns false

			shouldThrow<InvalidFileSpecificationsException> {
				fileSplitterServiceImpl.splitFiles(splitCommand)
			}
		}

		it("invalid file size") {
			this@describe.withData(1L, 10L) { length ->
				val splitCommand = SplitCommand()
				splitCommand.path = Paths.get("test")
				splitCommand.chunkSize = ChunkSize(10, ChunkSizeUnit.B)

				every { fileDao.isFile(splitCommand.path.toFile()) } returns true
				every { fileDao.getLength(splitCommand.path.toFile()) } returns length

				shouldThrow<InvalidFileSpecificationsException> {
					fileSplitterServiceImpl.splitFiles(splitCommand)
				}
			}
		}

		it("makes 2 exact splits") {
			val splitCommand = SplitCommand()
			splitCommand.path = Paths.get("test/file")
			splitCommand.chunkSize = ChunkSize(10, ChunkSizeUnit.B)

			val file = splitCommand.path.toFile()
			val fileLength = 20

			val inputArr = ByteArray(fileLength)
			val outputArr1 = ByteArrayOutputStream(10)
			val outputArr2 = ByteArrayOutputStream(10)

			val outputFile1 = File("test/file_000000")
			val outputFile2 = File("test/file_000001")

			Random.nextBytes(inputArr)

			every { fileDao.isFile(file) } returns true
			every { fileDao.getLength(file) } returns fileLength.toLong()
			every { fileDao.openForRead(file) } returnsMany listOf(inputArr.inputStream(), inputArr.inputStream())

			every { fileDao.openForWrite(outputFile1) } returns outputArr1
			every { fileDao.openForWrite(outputFile2) } returns outputArr2

			fileSplitterServiceImpl.splitFiles(splitCommand)

			outputArr1.toByteArray() shouldBe inputArr.copyOfRange(0, 10)
			outputArr2.toByteArray() shouldBe inputArr.copyOfRange(10, 20)
		}

		it("makes 2 non-exact splits") {
			val splitCommand = SplitCommand()
			splitCommand.path = Paths.get("test/file")
			splitCommand.chunkSize = ChunkSize(10, ChunkSizeUnit.B)

			val file = splitCommand.path.toFile()
			val fileLength = 15

			val inputArr = ByteArray(fileLength)
			val outputArr1 = ByteArrayOutputStream(10)
			val outputArr2 = ByteArrayOutputStream(5)

			val outputFile1 = File("test/file_000000")
			val outputFile2 = File("test/file_000001")

			Random.nextBytes(inputArr)

			every { fileDao.isFile(file) } returns true
			every { fileDao.getLength(file) } returns fileLength.toLong()
			every { fileDao.openForRead(file) } returnsMany listOf(inputArr.inputStream(), inputArr.inputStream())

			every { fileDao.openForWrite(outputFile1) } returns outputArr1
			every { fileDao.openForWrite(outputFile2) } returns outputArr2

			fileSplitterServiceImpl.splitFiles(splitCommand)

			outputArr1.toByteArray() shouldBe inputArr.copyOfRange(0, 10)
			outputArr2.toByteArray() shouldBe inputArr.copyOfRange(10, 15)
		}

		it("deletes original original file") {
			val splitCommand = SplitCommand()
			splitCommand.path = Paths.get("test/file")
			splitCommand.chunkSize = ChunkSize(10, ChunkSizeUnit.B)
			splitCommand.deleteOriginal = true

			val file = splitCommand.path.toFile()
			val fileLength = 20

			val inputArr = ByteArray(fileLength)
			val outputArr1 = ByteArrayOutputStream(10)
			val outputArr2 = ByteArrayOutputStream(10)

			val outputFile1 = File("test/file_000000")
			val outputFile2 = File("test/file_000001")

			Random.nextBytes(inputArr)

			every { fileDao.isFile(file) } returns true
			every { fileDao.getLength(file) } returns fileLength.toLong()
			every { fileDao.openForRead(file) } returnsMany listOf(inputArr.inputStream(), inputArr.inputStream())
			every { fileDao.delete(file) } returns true

			every { fileDao.openForWrite(outputFile1) } returns outputArr1
			every { fileDao.openForWrite(outputFile2) } returns outputArr2

			fileSplitterServiceImpl.splitFiles(splitCommand)

			verify(exactly = 1) { fileDao.delete(file) }
		}
	}

	describe("combineFiles") {

		it("combines 2 files") {
			val inputFile1 = File("test/file_000000")
			val inputFile2 = File("test/file_000001")
			val combineCommand = CombineCommand()
			combineCommand.destinationName = Paths.get("test/file")
			combineCommand.paths = arrayOf(inputFile1.toPath(), inputFile2.toPath())

			val file = combineCommand.destinationName.toFile()
			val fileLength = 20

			val outputArr = ByteArrayOutputStream(fileLength)
			val inputArr1 = ByteArray(10)
			val inputArr2 = ByteArray(10)

			Random.nextBytes(inputArr1)
			Random.nextBytes(inputArr2)

			every { fileDao.openForWrite(file) } returns outputArr
			every { fileDao.getLength(inputFile1) } returns 10
			every { fileDao.getLength(inputFile2) } returns 10
			every { fileDao.openForRead(inputFile1) } returns inputArr1.inputStream()
			every { fileDao.openForRead(inputFile2) } returns inputArr2.inputStream()
			every { fileDao.openForRead(file) } returns outputArr.toByteArray().inputStream()

			fileSplitterServiceImpl.combineFiles(combineCommand)

			outputArr.toByteArray().copyOfRange(0, 10) shouldBe inputArr1
			outputArr.toByteArray().copyOfRange(10, 20) shouldBe inputArr2
		}

		it("deletes original file") {
			val inputFile1 = File("test/file_000000")
			val inputFile2 = File("test/file_000001")
			val combineCommand = CombineCommand()
			combineCommand.destinationName = Paths.get("test/file")
			combineCommand.paths = arrayOf(inputFile1.toPath(), inputFile2.toPath())
			combineCommand.deleteChunk = true

			val file = combineCommand.destinationName.toFile()
			val fileLength = 20

			val outputArr = ByteArrayOutputStream(fileLength)
			val inputArr1 = ByteArray(10)
			val inputArr2 = ByteArray(10)

			Random.nextBytes(inputArr1)
			Random.nextBytes(inputArr2)

			every { fileDao.openForWrite(file) } returns outputArr
			every { fileDao.getLength(inputFile1) } returns 10
			every { fileDao.getLength(inputFile2) } returns 10
			every { fileDao.openForRead(inputFile1) } returns inputArr1.inputStream()
			every { fileDao.openForRead(inputFile2) } returns inputArr2.inputStream()
			every { fileDao.openForRead(file) } returns outputArr.toByteArray().inputStream()
			every { fileDao.delete(inputFile1) } returns true
			every { fileDao.delete(inputFile2) } returns true

			fileSplitterServiceImpl.combineFiles(combineCommand)

			verify(exactly = 1) { fileDao.delete(inputFile1) }
			verify(exactly = 1) { fileDao.delete(inputFile2) }
		}
	}
})