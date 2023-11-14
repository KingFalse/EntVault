package com.cnwy.crawler.util;

import com.google.common.net.InternetDomainName;

import java.net.MalformedURLException;
import java.net.URL;

public class DomainUtil {

    /**
     * 从给定的URL中提取二级域名
     *
     * @param link
     * @return
     * @throws MalformedURLException
     */
    public static String getDomainFromURL(String link) throws MalformedURLException {
        return new URL(link).getHost();
    }

    /**
     * 从URL中提取根域名
     *
     * @param link
     * @return
     * @throws MalformedURLException
     */
    public static String getRootDomainFromURL(String link) throws MalformedURLException {
        return InternetDomainName.from(getDomainFromURL(link)).topPrivateDomain().toString();
    }

    public static void main(String[] args) throws MalformedURLException {
        String url = "http://1234.news.sina.com.cn/abc/dfe?sad=12&sd=4";
        System.err.println(getDomainFromURL(url));
        System.err.println(getRootDomainFromURL(url));
    }
}
