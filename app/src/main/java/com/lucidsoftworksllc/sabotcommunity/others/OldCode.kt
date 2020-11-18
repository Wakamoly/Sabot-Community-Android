package com.lucidsoftworksllc.sabotcommunity.others

import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*val bodybits = profilenews.body.split("\\s+".toRegex()).toTypedArray()
            for (item in bodybits) {
                if (Patterns.WEB_URL.matcher(item).matches()) {
                    val finalItem: String = if (!item.contains("http://") && !item.contains("https://")) {
                        "https://$item"
                    } else {
                        if (item.contains("http://")){
                            item.replace("http://","https://")
                        }else{
                            item
                        }
                    }
                    val imageUrl = arrayOfNulls<String>(1)
                    val title = arrayOfNulls<String>(1)
                    val desc = arrayOfNulls<String>(1)
                    urlPreview.visible(true)
                    urlImage.setOnClickListener {
                        val uri = Uri.parse(finalItem)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        mCtx.startActivity(intent)
                    }
                        try {
                            CoroutineScope(IO).launch {
                                val doc = Jsoup.connect(finalItem).get()
                                val ogTags = doc.select("meta[property^=og:]")
                                if (ogTags.size <= 0) {
                                    return@launch
                                }
                                val metaOgTitle = doc.select("meta[property=og:title]")
                                if (metaOgTitle != null) {
                                    title[0] = metaOgTitle.attr("content")
                                } else {
                                    title[0] = doc.title()
                                }
                                val metaOgDesc = doc.select("meta[property=og:description]")
                                if (metaOgDesc != null) {
                                    desc[0] = metaOgDesc.attr("content")
                                }
                                val metaOgImage = doc.select("meta[property=og:image]")
                                if (metaOgImage != null) {
                                    imageUrl[0] = metaOgImage.attr("content")
                                }
                                addPostLinkBits(imageUrl[0], title[0], desc[0])
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    break
                } else {
                    urlPreview.visible(false)
                }
                break
            }*/

/*val pattern: Pattern = Pattern.compile(WEB_URL.pattern())
            val matcher: Matcher = pattern.matcher(profilenews.body)
            if (matcher.find()) {
                val item = profilenews.body.substring(matcher.start(), matcher.end())
                val finalItem: String = if (!item.contains("http://") && !item.contains("https://")) {
                    "https://$item"
                } else {
                    if (item.contains("http://")) {
                        item.replace("http://", "https://")
                    } else {
                        item
                    }
                }
                if (item.contains("https://")) {
                    var imageUrl: String? = null
                    var title: String? = null
                    var desc: String? = null
                    urlPreview.visible(true)
                    urlImage.setOnClickListener {
                        val uri = Uri.parse(finalItem)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        mCtx.startActivity(intent)
                    }

                    CoroutineScope(IO).launch {
                        try {
                            val doc = Jsoup.connect(finalItem).get()
                            val ogTag = doc.select("meta[property^=og:]").first()
                                    ?: return@launch
                            title = doc.title()
                            val metaOgDesc = doc.select("meta[property=og:description]")
                            if (metaOgDesc != null) {
                                desc = metaOgDesc.attr("content")
                            }
                            val metaOgImage = doc.select("meta[property=og:image]")
                            if (metaOgImage != null) {
                                imageUrl = metaOgImage.attr("content")
                            }
                            addPostLinkBits(imageUrl, title, desc)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                } else {
                    urlPreview.visible(false)
                }

            } else {
                urlPreview.visible(false)
            }*/

/*
private fun addPostLinkBits(imageUrl: String?, title: String?, desc: String?){
    CoroutineScope(Dispatchers.Main).launch {
        if (imageUrl.isNullOrEmpty()) {
            urlImage.setImageResource(R.drawable.ic_error)
        } else {
            Glide.with(mCtx)
                    .load(imageUrl)
                    .error(R.drawable.ic_error)
                    .into(urlImage)
        }
        urlTitle.text = title ?: ""
        urlDesc.text = desc ?: ""
        urlProgress.visible(false)
        urlBits.visible(true)
    }
}*/
