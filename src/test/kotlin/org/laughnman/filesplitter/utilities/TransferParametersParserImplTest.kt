package org.laughnman.filesplitter.utilities

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.contain
import org.laughnman.filesplitter.models.transfer.FileDestinationParameters
import org.laughnman.filesplitter.models.transfer.FileSourceParameters
import org.laughnman.filesplitter.models.transfer.TesterParameters
import org.laughnman.filesplitter.models.transfer.TransferType

class TransferParametersParserImplTest : DescribeSpec ({

	val transferParametersParserImpl = TransferParametersParserImpl()

	describe("given bad a bad type") {
		it("with an empty string") {
			val e = shouldThrow<RuntimeException> {
				transferParametersParserImpl.parse(Direction.SOURCE, "")
			}
			e.message should contain("type is unknown")
		}

		it("with an invalid type") {
			val e = shouldThrow<RuntimeException> {
				transferParametersParserImpl.parse(Direction.SOURCE, "type=blah,test=test")
			}
			e.message should contain("blah type is unknown")
		}
	}

	describe("given invalid parameters") {
		it("with missing required parameters") {
			val e = shouldThrow<RuntimeException> {
				transferParametersParserImpl.parse(Direction.SOURCE, "type=tester")
			}
			e.message should contain("Parameter intField is required.")
			e.message should contain("Parameter floatField is required.")
		}

		it("with extra parameters") {
			val e = shouldThrow<RuntimeException> {
				transferParametersParserImpl.parse(Direction.SOURCE,
					"type=tester,intField=0,longField=0,floatField=0,doubleField=0,stringField=blah,blahField1=blah,blahField2=blah")
			}
			e.message should contain("Unknown parameter blahField1")
			e.message should contain("Unknown parameter blahField2")
		}
	}

	describe("given good values") {
		it("creates TesterParameters object") {
			val t: TesterParameters = transferParametersParserImpl.parse(Direction.SOURCE,
				"type=tester,intField=0,longField=1,floatField=2.0,doubleField=3.0,stringField=str")

			t.type.shouldBe(TransferType.TESTER)
			t.intField.shouldBe(0)
			t.longField.shouldBe(1L)
			t.floatField.shouldBe(2.0F)
			t.doubleField.shouldBe(3.0)
			t.stringField.shouldBe("str")
		}

		it("creates FileSourceParameters object") {
			val t: FileSourceParameters = transferParametersParserImpl.parse(Direction.SOURCE,
				"type=file,path=path")

			t.type.shouldBe(TransferType.FILE)
			t.path.shouldBe("path")
		}

		it("creates FileDestinationParameters object") {
			val t: FileDestinationParameters = transferParametersParserImpl.parse(Direction.DESTINATION,
				"type=file,path=path")

			t.type.shouldBe(TransferType.FILE)
			t.path.shouldBe("path")
		}

	}


})