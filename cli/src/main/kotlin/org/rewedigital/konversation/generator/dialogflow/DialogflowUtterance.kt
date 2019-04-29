package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.forEachIterator
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

data class DialogflowUtterance(
    val count: Int,
    val `data`: List<UtterancePart>,
    val id: UUID,
    val isTemplate: Boolean,
    val updated: Long) : NodeExporter {

    override fun prettyPrinted(printer: Printer) {
        printer("  {\n")
        printer("    \"id\": \"$id\",\n")
        printer("    \"data\": [")
        if (`data`.isNotEmpty()) {
            printer("\n")
            data.forEachIterator {
                it.prettyPrinted(printer)
                if (hasNext()) {
                    printer(",")
                }
                printer("\n")
            }
            printer("    ")
        }
        printer("],\n")
        printer("    \"isTemplate\": $isTemplate,\n")
        //printer("    \"updated\": $updated\n")
        printer("    \"count\": $count\n")
        printer("  }")
    }

    override fun minified(printer: Printer) {
        printer("{\"count\":$count,\"data\":[")
        data.forEachIterator {
            it.minified(printer)
            if (hasNext()) {
                printer(",")
            }
        }
        printer("],")
        printer("\"id\":\"$id\",")
        printer("\"isTemplate\":$isTemplate,")
        printer("\"updated\":$updated")
        printer("}")
    }

    data class UtterancePart(
        val text: String,
        val alias: String? = null,
        val meta: String? = null,
        val userDefined: Boolean) : NodeExporter {

        override fun prettyPrinted(printer: Printer) {
            printer("      {\n")
            printer("        \"text\": \"$text\",\n")
            alias?.let {
                printer("        \"alias\": \"$alias\",\n")
            }
            meta?.let {
                printer("        \"meta\": \"$meta\",\n")
            }
            printer("        \"userDefined\": $userDefined\n")
            printer("      }")
        }

        override fun minified(printer: Printer) {
            printer("{\"text\":\"$text\"")
            alias?.let {
                printer(",\"alias\":\"$alias\"")
            }
            meta?.let {
                printer(",\"meta\":\"$meta\"")
            }
            printer(",\"userDefined\":$userDefined}")
        }
    }
}

/*
Error processing intent from file: 'intents/REWE.shopping.search.product.in.category_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.reorder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.product.details_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.delivery.time_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.delete_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.cart.price_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.myproducts.show_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.gradingborder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.topicworld_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.bulkysurcharge_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.add_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.new_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.cart.add_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.checkout_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.show_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.minimumorder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.change_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.myproducts_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.offer_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.price_usersays_de.json'. Uploaded file contains invalid Intent.


Error processing intent from file: 'intents/REWE.shopping.search.product.in.category_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.reorder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.product.details_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.delivery.time_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.delete_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.cart.price_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.myproducts.show_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.gradingborder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.topicworld_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.bulkysurcharge_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.add_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.new_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.cart.add_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.checkout_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.favorites.show_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.minimumorder_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.order.change_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.myproducts_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.offer_usersays_de.json'. Uploaded file contains invalid Intent.
Error processing intent from file: 'intents/REWE.shopping.search.price_usersays_de.json'. Uploaded file contains invalid Intent.
 */