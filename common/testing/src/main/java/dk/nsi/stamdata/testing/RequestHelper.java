/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package dk.nsi.stamdata.testing;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHelper {

    public static String sendRequest(String url, String action, String docXml, boolean failOnError) throws IOException {
        HttpURLConnection uc = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            URL u = new URL(url);
            uc = (HttpURLConnection) u.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestMethod("POST");
            uc.setRequestProperty("SOAPAction", "\"" + action + "\"");
            uc.setRequestProperty("Content-Type", "text/xml; encoding=utf-8");
            os = uc.getOutputStream();
            IOUtils.write(docXml, os, "UTF-8");
            os.flush();
            if (uc.getResponseCode() != 200) {
                is = uc.getErrorStream();
            } else {
                is = uc.getInputStream();
            }
            String res = IOUtils.toString(is, "UTF-8");
            if (uc.getResponseCode() != 200 && (uc.getResponseCode() != 500 || failOnError)) {
                throw new RuntimeException("Got unexpected response " + uc.getResponseCode() +" from " + url);
            }
            return res;
        } finally {
            if (os != null) IOUtils.closeQuietly(os);
            if (is != null) IOUtils.closeQuietly(is);
            if (uc != null) uc.disconnect();
        }
    }

}
