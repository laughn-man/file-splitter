package org.laughnman.multitransfer.services.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.TransferCommand

interface TransferService {

	fun runTransfer(transferCommand: TransferCommand, sourceCommand: AbstractCommand, destinationCommand: AbstractCommand)
}