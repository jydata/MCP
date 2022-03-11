package com.jiuye.mcp.agent.util;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * 解密工具类
 *
 * @author kevin
 * @date 2018-08-28
 */
public class DecryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(DecryptUtil.class.getName());

    private DecryptUtil() {
    }

    private static class Decrypt {
        private final static DecryptUtil instance = new DecryptUtil();
    }

    public static DecryptUtil getInstance() {
        return Decrypt.instance;
    }

    /**
     * 解密
     *
     * @param decryptPwd
     * @return
     * @throws BizException
     */
    public String decrypt(String decryptPwd) throws BizException {
        String decrypt = "";
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "mcp-view.jks");
            FileInputStream in = new FileInputStream(cfgFile);
            keyStore.load(in, "jydata".toCharArray());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey("mcp-view", "jydata".toCharArray()));
            byte[] bs = decoder.decodeBuffer(decryptPwd);
            decrypt = new String(cipher.doFinal(bs));
        } catch (Exception e) {
            logger.error("Decrypt password is error!", e);
            throw new BizException(MetadataErrorCode.DECRYPT_ERROR.getCode(), MetadataErrorCode.DECRYPT_ERROR.getMessage());
        }

        return decrypt;
    }

}
