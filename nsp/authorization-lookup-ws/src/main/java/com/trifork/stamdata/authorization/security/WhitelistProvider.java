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
package com.trifork.stamdata.authorization.security;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

public class WhitelistProvider implements Provider<Set<String>>
{
    public static final TypeLiteral<Set<String>> SET_OF_STRINGS = new TypeLiteral<Set<String>>() {};
    private final Set<String> whitelist;

    @Inject
    WhitelistProvider(@Named("subjectSerialNumbers") String subjectSerialNumbers)
    {
        Iterable<String> serials = Splitter.on(',').omitEmptyStrings().trimResults().split(subjectSerialNumbers);
        List<String> cvrs = Lists.newArrayList();
        
        for (String serial : serials)
        {
            String cvr = serial.split(":")[1];
            cvr = cvr.split("-")[0];
            cvrs.add(cvr);
        }
        
        whitelist = ImmutableSet.copyOf(cvrs);
    }
    
    @Override
    public Set<String> get() {
        
        return whitelist;
    }

}
