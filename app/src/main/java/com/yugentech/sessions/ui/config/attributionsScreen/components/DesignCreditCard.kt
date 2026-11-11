package com.yugentech.sessions.ui.config.attributionsScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.utils.AppConstants.PABLO_STANLEY_URL

@Composable
fun DesignCreditCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.s)
    ) {
        // Bueno stands on the card's bottom edge on the leading side: the drawable is trimmed so
        // the lowest point of the art is its bottom, and nothing pads it there. The text keeps its own
        // column beside it, so the art never sits behind any of it. Its own black and white
        // reads on the card in light and dark.
        // Fills the card when the carousel stretches it to the taller card, so Bueno stays
        // on the bottom edge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            Image(
                painter = painterResource(R.drawable.illustration_bueno),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.l, top = MaterialTheme.spacing.l)
                    .width(IllustrationWidth)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = MaterialTheme.spacing.m,
                        top = MaterialTheme.spacing.l,
                        end = MaterialTheme.spacing.l,
                        bottom = MaterialTheme.spacing.xl
                    ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
            ) {
                Column {
                    Text(
                        stringResource(R.string.credit_design_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        stringResource(R.string.credit_design_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    // His name on its own line, larger, as a link to his site
                    val artist = stringResource(R.string.credit_design_artist)
                    val linkColor = MaterialTheme.colorScheme.secondary
                    Text(
                        text = buildAnnotatedString {
                            withLink(
                                LinkAnnotation.Url(
                                    url = PABLO_STANLEY_URL,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = linkColor,
                                            textDecoration = TextDecoration.Underline
                                        ),
                                        pressedStyle = SpanStyle(
                                            color = linkColor.copy(alpha = 0.6f),
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                )
                            ) { append(artist) }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.xs)
                    )
                }

                Text(
                    stringResource(R.string.credit_design_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    minLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Bueno's width beside the text; its height follows from the art (about 1.35 times as tall). */
private val IllustrationWidth = 144.dp
