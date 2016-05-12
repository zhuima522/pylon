package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.YotouchApplication;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeChatServiceImpl  {

    static final private Logger logger = LoggerFactory.getLogger(WeChatServiceImpl.class);

    private YotouchApplication ytApp;
    private WxMpInMemoryConfigStorage mpConfig;
    private WxMpService mpService;
    private WxMpMessageRouter wxMpMessageRouter;
    
    private Entity wechat;
    
    public WeChatServiceImpl(YotouchApplication ytApp, Entity wechat) {
        this.ytApp = ytApp;
        this.wechat = wechat;

        mpConfig = new WxMpInMemoryConfigStorage();

        mpConfig.setAppId(wechat.v("appId"));   // 设置微信公众号的appid
        mpConfig.setSecret(wechat.v("secret")); // 设置微信公众号的app corpSecret
        mpConfig.setToken(wechat.v("token"));   // 设置微信公众号的token
        mpConfig.setAesKey(wechat.v("aeskey")); // 设置微信公众号的EncodingAESKey

        mpService = new WxMpServiceImpl();
        mpService.setWxMpConfigStorage(mpConfig);

        wxMpMessageRouter = new WxMpMessageRouter(mpService);
        
        
    }
    
    public Entity getWechatEntity() {
        return this.wechat;
    }

    public String genAuthUrl(String baseUrl, String url, String state) {
        
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        //String myHost = (String) ytApp.getProp("host");
        String fullUrl = baseUrl + "/connect/wechat/oauthCallback?url=" + url;

        /*
        try {
            fullUrl = URLEncoder.encode(fullUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
        
        logger.info("Gen wechat auth url " + fullUrl);
        

        String redirectUrl = this.mpService.oauth2buildAuthorizationUrl(fullUrl, WxConsts.OAUTH2_SCOPE_USER_INFO,
                state);

        logger.info("Gen wechat redirect auth url " + redirectUrl);
        return redirectUrl;

    }

    public void setMessageHandler(WxMpMessageHandler msgHandler) {
        wxMpMessageRouter.rule().async(false).handler(msgHandler).end();
    }

    public boolean checkSignature(String timestamp, String nonce, String signature) {
        return this.mpService.checkSignature(timestamp, nonce, signature);
    }

    public WxMpConfigStorage getWechatConfig() {
        return this.mpConfig;
    }

    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException {
        return this.mpService.oauth2getAccessToken(code);
    }

    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken accessToken) throws WxErrorException {
        return this.mpService.oauth2getUserInfo(accessToken, "");
    }

    public WxMpXmlOutMessage route(WxMpXmlMessage inMsg) {
        return this.wxMpMessageRouter.route(inMsg);
    }

    public WxJsapiSignature createJsapiSignature(String fullUrl) throws WxErrorException {
        WxJsapiSignature jss = this.mpService.createJsapiSignature(fullUrl);
        return jss;        
    }


}