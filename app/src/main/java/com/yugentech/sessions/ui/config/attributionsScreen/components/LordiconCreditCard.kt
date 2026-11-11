package com.yugentech.sessions.ui.config.attributionsScreen.components

import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.ui.components.TintedLottieIcon
import androidx.compose.ui.res.stringResource
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun LordiconCreditCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lordiconUrl = "https://lordicon.com/"

    Card(
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.s)
    ) {
        // Fills the card when the carousel stretches it to the taller card; the button then
        // sits at the bottom, with the text at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(MaterialTheme.components.imageSizeSmall)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        TintedLottieIcon(
                            animation = R.raw.attribution_icon,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(MaterialTheme.icons.large)
                        )
                    }
                }

                Spacer(Modifier.width(MaterialTheme.spacing.m))

                Column {
                    Text(
                        stringResource(R.string.credit_lordicon_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        stringResource(R.string.credit_lordicon_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Text(
                stringResource(R.string.credit_lordicon_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                minLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            }

            FilledTonalButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, lordiconUrl.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = MaterialTheme.spacing.m),
                shape = RoundedCornerShape(MaterialTheme.corners.medium),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(stringResource(R.string.credit_lordicon_action))
            }
        }
    }
}