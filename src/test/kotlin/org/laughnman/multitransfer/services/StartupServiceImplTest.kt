package org.laughnman.multitransfer.services

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.laughnman.multitransfer.models.*
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.TransferService
import java.net.URI
import kotlin.io.path.Path

class StartupServiceImplTest : DescribeSpec({

	val fileSplitterService = mockk<FileSplitterService>(relaxed = true)
	val transferService = mockk<TransferService>(relaxed = true)

	val startupServiceImpl = StartupServiceImpl(fileSplitterService, transferService)

	afterTest {
		clearMocks(fileSplitterService)
		clearMocks(transferService)
	}

	describe("Main arguments") {
		it("version") {
			startupServiceImpl.run(arrayOf("--version")).shouldBe(0)
			startupServiceImpl.run(arrayOf("-V")).shouldBe(0)
		}

		it("help") {
			startupServiceImpl.run(arrayOf("--help")).shouldBe(0)
			startupServiceImpl.run(arrayOf("-h")).shouldBe(0)
		}
	}

	describe("split") {
		it("default") {
			startupServiceImpl.run(arrayOf("split", "blah")).shouldBe(0)

			val splitCommand = SplitCommand().apply {
				path = Path("blah")
				call()
			}

		  verify(exactly = 1) { fileSplitterService.splitFiles(splitCommand) }

			confirmVerified(fileSplitterService)
		}

		it("no parameters") {
			startupServiceImpl.run(arrayOf("split")).shouldNotBe(0)
		}

		it("size") {
			startupServiceImpl.run(arrayOf("split", "blah", "--size", "5TB")).shouldBe(0)

			val splitCommand = SplitCommand().apply {
				path = Path("blah")
				chunkSize = ChunkSize(5, ChunkSizeUnit.TB)
				call()
			}

			verify(exactly = 1) { fileSplitterService.splitFiles(splitCommand) }

			confirmVerified(fileSplitterService)
		}

		it("delete original") {
			startupServiceImpl.run(arrayOf("split", "blah", "--size", "5TB", "--delete-original")).shouldBe(0)

			val splitCommand = SplitCommand().apply {
				path = Path("blah")
				chunkSize = ChunkSize(5, ChunkSizeUnit.TB)
				deleteOriginal = true
				call()
			}

			verify(exactly = 1) { fileSplitterService.splitFiles(splitCommand) }

			confirmVerified(fileSplitterService)
		}
	}

	describe("combine") {
		it("default") {
			startupServiceImpl.run(arrayOf("combine", "blah", "chunk1", "chunk2", "chunk3")).shouldBe(0)

			val combineCommand = CombineCommand().apply {
				destinationName = Path("blah")
				paths = arrayOf(Path("chunk1"), Path("chunk2"), Path("chunk3"))
				call()
			}

			verify(exactly = 1) { fileSplitterService.combineFiles(combineCommand) }
			confirmVerified(fileSplitterService)
		}

		it("no parameters") {
			startupServiceImpl.run(arrayOf("combine")).shouldNotBe(0)
		}

		it("no paths") {
			startupServiceImpl.run(arrayOf("combine", "blah")).shouldNotBe(0)
		}

		it("delete chunk") {
			startupServiceImpl.run(arrayOf("combine", "blah", "chunk1", "chunk2", "chunk3", "--delete-chunk")).shouldBe(0)

			val combineCommand = CombineCommand().apply {
				destinationName = Path("blah")
				paths = arrayOf(Path("chunk1"), Path("chunk2"), Path("chunk3"))
				deleteChunk = true
				call()
			}

			verify(exactly = 1) { fileSplitterService.combineFiles(combineCommand) }
			confirmVerified(fileSplitterService)
		}
	}

	describe("transfer") {
		describe("file to file") {
			it("default") {
				startupServiceImpl.run(arrayOf("transfer", "src-file", "in", "dest-file", "out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = FileSourceCommand().apply {
					filePaths = arrayOf(Path("in"))
					call()
				}
				val destinationCommand = FileDestinationCommand().apply {
					path = Path("out")
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("with parallelism of 5") {
				startupServiceImpl.run(arrayOf("transfer", "--parallelism", "5", "src-file", "in", "dest-file", "out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					parallelism = 5
					call()
				}
				val sourceCommand = FileSourceCommand().apply {
					filePaths = arrayOf(Path("in"))
					call()
				}
				val destinationCommand = FileDestinationCommand().apply {
					path = Path("out")
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("with buffer size of 1GB") {
				startupServiceImpl.run(arrayOf("transfer", "--parallelism", "5", "--buffer-size", "1GB", "src-file", "in", "dest-file", "out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					parallelism = 5
					bufferSize = ChunkSize(1, ChunkSizeUnit.GB)
					call()
				}
				val sourceCommand = FileSourceCommand().apply {
					filePaths = arrayOf(Path("in"))
					call()
				}
				val destinationCommand = FileDestinationCommand().apply {
					path = Path("out")
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("no source file") {
				startupServiceImpl.run(arrayOf("transfer", "src-file", "dest-file", "out")).shouldNotBe(0)
			}

			it("no dest file") {
				startupServiceImpl.run(arrayOf("transfer", "src-file", "in", "dest-file")).shouldNotBe(0)
			}

			it("no dest command") {
				startupServiceImpl.run(arrayOf("transfer", "src-file", "in")).shouldNotBe(0)
			}

			it("all dest commands") {
				startupServiceImpl.run(arrayOf("transfer", "dest-file", "in", "dest-file", "out")).shouldNotBe(0)
			}
		}

		describe("Artifactory to Artifactory") {
			it("default") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--password", "inpass",
					"dest-artifactory", "out", "--url", "outurl", "--password", "outpass")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = ArtifactorySourceCommand().apply {
					filePaths = arrayOf("in1", "in2")
					url = URI.create("inurl")
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "inpass"
					}
					call()
				}
				val destinationCommand = ArtifactoryDestinationCommand().apply {
					filePath = "out"
					url = URI.create("outurl")
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "outpass"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with token") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--token", "intoken",
					"dest-artifactory", "out", "--url", "outurl", "--token", "outtoken")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = ArtifactorySourceCommand().apply {
					filePaths = arrayOf("in1", "in2")
					url = URI.create("inurl")
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						token = "intoken"
					}
					call()
				}
				val destinationCommand = ArtifactoryDestinationCommand().apply {
					filePath = "out"
					url = URI.create("outurl")
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						token = "outtoken"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("with both token and password with token") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--password", "inpass", "--token", "intoken",
					"dest-artifactory", "out", "--url", "outurl", "--password", "outpass", "--token", "outtoken")).shouldNotBe(0)
			}

			it("default with user") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--user", "inuser", "--password", "inpass",
					"dest-artifactory", "out", "--url", "outurl", "--user", "outuser", "--password", "outpass")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = ArtifactorySourceCommand().apply {
					filePaths = arrayOf("in1", "in2")
					url = URI.create("inurl")
					userName = "inuser"
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "inpass"
					}
					call()
				}
				val destinationCommand = ArtifactoryDestinationCommand().apply {
					filePath = "out"
					url = URI.create("outurl")
					userName = "outuser"
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "outpass"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with request timeout") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--request-timeout", "50", "--password", "inpass",
					"dest-artifactory", "out", "--url", "outurl", "--request-timeout", "100", "--password", "outpass")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = ArtifactorySourceCommand().apply {
					filePaths = arrayOf("in1", "in2")
					url = URI.create("inurl")
					requestTimeout = 50
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "inpass"
					}
					call()
				}
				val destinationCommand = ArtifactoryDestinationCommand().apply {
					filePath = "out"
					url = URI.create("outurl")
					requestTimeout = 100
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "outpass"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with insecure") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--insecure", "--password", "inpass",
					"dest-artifactory", "out", "--url", "outurl", "--insecure", "--password", "outpass")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = ArtifactorySourceCommand().apply {
					filePaths = arrayOf("in1", "in2")
					url = URI.create("inurl")
					insecure = true
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "inpass"
					}
					call()
				}
				val destinationCommand = ArtifactoryDestinationCommand().apply {
					filePath = "out"
					url = URI.create("outurl")
					insecure = true
					exclusive = AbstractArtifactoryCommand.Exclusive().apply {
						password = "outpass"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("with no input files") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "--url", "inurl", "--password", "inpass", "--token", "intoken",
					"dest-artifactory", "out", "--url", "outurl", "--password", "outpass", "--token", "outtoken")).shouldNotBe(0)
			}

			it("with no output files") {
				startupServiceImpl.run(arrayOf("transfer", "src-artifactory", "in1", "in2", "--url", "inurl", "--password", "inpass", "--token", "intoken",
					"dest-artifactory", "--url", "outurl", "--password", "outpass", "--token", "outtoken")).shouldNotBe(0)
			}
		}

		describe("S3 to S3") {
			it("default") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "dest-s3", "s3://bucket/out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = S3SourceCommand().apply {
					s3Urls = arrayOf(S3Url("bucket", "in"))
					call()
				}
				val destinationCommand = S3DestinationCommand().apply {
					s3Url = S3Url("bucket", "out")
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with profile") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "--profile", "in",
					"dest-s3", "s3://bucket/out", "--profile", "out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = S3SourceCommand().apply {
					s3Urls = arrayOf(S3Url("bucket", "in"))
					exclusive = AbstractS3Command.Exclusive().apply {
						profile = "in"
					}
					call()
				}
				val destinationCommand = S3DestinationCommand().apply {
					s3Url = S3Url("bucket", "out")
					exclusive = AbstractS3Command.Exclusive().apply {
						profile = "out"
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with access keys") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "--access-key", "keyin", "--access-secret", "secin",
					"dest-s3", "s3://bucket/out", "--access-key", "keyout", "--access-secret", "secout")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = S3SourceCommand().apply {
					s3Urls = arrayOf(S3Url("bucket", "in"))
					exclusive = AbstractS3Command.Exclusive().apply {
						access = AbstractS3Command.Access().apply {
							accessKey = "keyin"
							accessSecret = "secin"
						}
					}
					call()
				}
				val destinationCommand = S3DestinationCommand().apply {
					s3Url = S3Url("bucket", "out")
					exclusive = AbstractS3Command.Exclusive().apply {
						access = AbstractS3Command.Access().apply {
							accessKey = "keyout"
							accessSecret = "secout"
						}
					}
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("both profile and access keys") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "--profile", "proin", "--access-key", "keyin", "--access-secret", "secin",
					"dest-s3", "s3://bucket/out", "--profile", "proout", "--access-key", "keyout", "--access-secret", "secout")).shouldNotBe(0)
			}

			it("default with region") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "--region", "regin", "dest-s3", "s3://bucket/out", "--region", "regout")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = S3SourceCommand().apply {
					s3Urls = arrayOf(S3Url("bucket", "in"))
					region = "regin"
					call()
				}
				val destinationCommand = S3DestinationCommand().apply {
					s3Url = S3Url("bucket", "out")
					region = "regout"
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("default with endpoint") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "--endpoint", "in", "dest-s3", "s3://bucket/out", "--endpoint", "out")).shouldBe(0)

				val transferCommand = TransferCommand().apply {
					call()
				}
				val sourceCommand = S3SourceCommand().apply {
					s3Urls = arrayOf(S3Url("bucket", "in"))
					endpoint = "in"
					call()
				}
				val destinationCommand = S3DestinationCommand().apply {
					s3Url = S3Url("bucket", "out")
					endpoint = "out"
					call()
				}

				verify(exactly = 1) { transferService.runTransfer(transferCommand, sourceCommand, destinationCommand) }
			}

			it("no source bucket") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "dest-s3", "s3://bucket/out")).shouldNotBe(0)
			}

			it("no dest bucket") {
				startupServiceImpl.run(arrayOf("transfer", "src-s3", "s3://bucket/in", "dest-s3")).shouldNotBe(0)
			}
		}
	}

})