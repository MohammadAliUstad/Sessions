package com.yugentech.sessions.utils

import com.yugentech.sessions.localization.UiText
import com.yugentech.sessions.R
import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class BillingManager(context: Context) {

    companion object {
        /** The event for a completed donation, which shows the thank-you dialog. */
        val ThankYou: UiText = UiText.Res(R.string.billing_thanks)
    }

    /** What to tell the user about a purchase; put into words where it's shown. */
    private val _purchaseEvent = MutableSharedFlow<UiText>()
    val purchaseEvent = _purchaseEvent.asSharedFlow()

    private var productDetailsCache: List<ProductDetails> = emptyList()

    private val productIds = listOf("donation_coffee", "donation_lunch")

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK if purchases != null -> {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("User canceled the purchase")
            }
            else -> {
                Timber.e("Billing error: ${result.debugMessage.ifBlank { "Unknown Error" }}")
                CoroutineScope(Dispatchers.Main).launch {
                    _purchaseEvent.emit(UiText.Res(R.string.billing_error))
                }
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun startConnection() {
        if (billingClient.isReady) {
            queryProducts()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Billing setup done")
                    queryProducts()
                } else {
                    // Transient Play Store issue — auto-resolves on retry, not user-actionable
                    Timber.e("Billing setup failed: ${billingResult.debugMessage}")
                }
            }
            override fun onBillingServiceDisconnected() {
                Timber.w("Billing service disconnected — retrying")
                startConnection()
            }
        })
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result: BillingResult, queryResult: QueryProductDetailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsCache = queryResult.productDetailsList
                Timber.d("Products fetched: ${queryResult.productDetailsList.size}")
            } else {
                Timber.e("Failed to query products: ${result.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val productDetails = productDetailsCache.find { it.productId == productId }

        if (productDetails != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            startConnection()
            CoroutineScope(Dispatchers.Main).launch {
                _purchaseEvent.emit(UiText.Res(R.string.billing_not_loaded))
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { result, _ ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Donation Consumed")
                    CoroutineScope(Dispatchers.Main).launch {
                        _purchaseEvent.emit(ThankYou)
                    }
                }
            }
        }
    }
}