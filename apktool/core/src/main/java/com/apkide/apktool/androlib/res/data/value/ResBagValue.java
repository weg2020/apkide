/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.apkide.apktool.androlib.res.data.value;

import com.apkide.apktool.androlib.exceptions.AndrolibException;
import com.apkide.apktool.androlib.res.data.ResResource;
import com.apkide.apktool.androlib.res.xml.ResValuesXmlSerializable;
import com.apkide.apktool.util.Duo;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class ResBagValue extends ResValue implements ResValuesXmlSerializable {
    protected final ResReferenceValue mParent;

    public ResBagValue(ResReferenceValue parent) {
        this.mParent = parent;
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        String type = res.getResSpec().getType().getName();
        if ("style".equals(type)) {
            new ResStyleValue(mParent, new Duo[0], null)
                    .serializeToResValuesXml(serializer, res);
            return;
        }
        if ("array".equals(type)) {
            new ResArrayValue(mParent, new Duo[0]).serializeToResValuesXml(
                    serializer, res);
            return;
        }
        if ("plurals".equals(type)) {
            new ResPluralsValue(mParent, new Duo[0]).serializeToResValuesXml(
                    serializer, res);
            return;
        }

        serializer.startTag(null, "item");
        serializer.attribute(null, "type", type);
        serializer.attribute(null, "name", res.getResSpec().getName());
        serializer.endTag(null, "item");
    }

    public ResReferenceValue getParent() {
        return mParent;
    }
}
