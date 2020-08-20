/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.xbup.xbprog.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.exbin.xbup.core.block.declaration.XBDeclaration;
import org.exbin.xbup.core.block.declaration.local.XBLFormatDecl;
import org.exbin.xbup.core.parser.XBParsingException;
import org.exbin.xbup.core.serial.XBSerializable;

/**
 * XBUP String-BNF Context-Free Grammar Rule
 *
 * @version 0.1.19 2010/06/04
 * @author ExBin Project (http://exbin.org)
 */
public class XBBNFGrammar implements XBSerializable {

    public static long[] XBUP_FORMATREV_CATALOGPATH = {0, 1, 1, 1, 0};
    public static long[] XBUP_BLOCKREV_CATALOGPATH = {0, 1, 1, 1, 0};
    private List<XBBNFGrammarRule> rules;
    private Map<String, XBBNFGrammarRule> nameMap; // Cache

    public XBBNFGrammar() {
        rules = new ArrayList<>();
        nameMap = new HashMap<>();
    }

    public XBDeclaration getXBDeclaration() {
        return new XBDeclaration(new XBLFormatDecl(XBUP_FORMATREV_CATALOGPATH));
    }

    private void clear() {
        getRules().clear();
    }

    public List<XBBNFGrammarRule> getRules() {
        return rules;
    }

    public void setRules(List<XBBNFGrammarRule> rules) {
        this.rules = rules;
    }

    public String generateGrammarDefinition() {
        StringBuilder builder = new StringBuilder();
        for (XBBNFGrammarRule rule : rules) {
            builder.append(rule.getRuleName());
            builder.append(" ::= ");
            builder.append(generateGrammarRuleDef(rule));
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    public String generateGrammarRuleDef(XBBNFGrammarRule rule) {
        StringBuilder builder = new StringBuilder();
        List<String> ruleItems = rule.getRules();
        if (rule.getTerminal() != null) {
            if (rule.isAltMode()) {
                String term = rule.getTerminal();
                for (int j = 0; j < term.length() / 2; j++) {
                    if (j > 0) {
                        builder.append(" | ");
                    }
                    char sChar = term.charAt(j * 2);
                    char eChar = term.charAt(j * 2 + 1);
                    if (sChar == eChar) {
                        builder.append("\"");
                        builder.append(sChar);
                        builder.append("\"");
                    } else {
                        builder.append("\"");
                        builder.append(sChar);
                        builder.append("\" .. \"");
                        builder.append(eChar);
                        builder.append("\"");
                    }
                }
            } else {
                builder.append("\"").append(rule.getTerminal().replaceAll("\"", "\\\"")).append("\"");
            }
        } else {
            for (int j = 0; j < ruleItems.size(); j++) {
                if (j > 0) {
                    if (rule.isAltMode()) {
                        builder.append(" | ");
                    } else {
                        builder.append(" ");
                    }
                }
                String subRule = ruleItems.get(j);
                builder.append(subRule);
            }
        }
        return builder.toString();
    }

    public void parseGrammarDefinition(String grammar) throws XBParsingException {
        String[] lines = grammar.split(System.getProperty("line.separator"));
        for (String line : lines) {
            String[] words = line.split(" ");
            if (words.length < 3) {
                throw new XBParsingException("Missing at least one component: " + line);
            }
            XBBNFGrammarRule rule = new XBBNFGrammarRule();
            rule.setRuleName(words[0]);
            if (!"::=".equals(words[1])) {
                throw new XBParsingException("Expected \"::=\" :" + line);
            }
            int j = 2;
            boolean merge = false;
            while (j < words.length) {

            }
        }
    }

    public void addRule(XBBNFGrammarRule rule) {
        rules.add(rule);
        nameMap.put(rule.getRuleName(), rule);
    }

    public XBBNFGrammarRule findRule(String ruleName) {
        return nameMap.get(ruleName);
    }

    /**
     * @return the nameMap
     */
    public Map<String, XBBNFGrammarRule> getNameMap() {
        return nameMap;
    }

    /**
     * @param nameMap the nameMap to set
     */
    public void setNameMap(Map<String, XBBNFGrammarRule> nameMap) {
        this.nameMap = nameMap;
    }
}
