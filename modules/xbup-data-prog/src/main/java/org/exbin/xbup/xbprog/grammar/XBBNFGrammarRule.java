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
import java.util.List;
import org.exbin.xbup.core.block.XBBlockType;
import org.exbin.xbup.core.block.XBFixedBlockType;
import org.exbin.xbup.core.block.declaration.XBDeclaration;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.serial.XBSerializable;

/**
 * XBUP String-BNF Context-Free Grammar Rule
 * http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form TODO: This is
 * simplified version
 *
 * Variant 1: ruleName ::= rule1 rule2 ... ruleN (Rule sequence) Variant 2:
 * ruleName ::= rule1 | rule2 | ... | ruleN (Rule alternatives) Variant 3:
 * ruleName ::= terminal (Terminal string) Variant 4: ruleName ::= charA1 ..
 * charAN | charB1 .. char BN | .. | charZ1 .. char ZN (Char ranges)
 *
 * @version 0.1.19 2010/06/04
 * @author ExBin Project (http://exbin.org)
 */
public class XBBNFGrammarRule implements XBSerializable {

    public static long[] XBUP_BLOCKREV_CATALOGPATH = {0, 1, 1, 2};

    private String ruleName;
    private List<String> rules;
    private String terminal;
    // In alternative mode terminal value is interpreted as sequence defined by pair of characters
    private boolean altMode;

    public XBBNFGrammarRule() {
        rules = new ArrayList<>();
        clear();
    }

    public XBDeclaration getXBDeclaration() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public XBBlockType getBlockType(XBCatalog catalog) {
        Long[] path = new Long[XBUP_BLOCKREV_CATALOGPATH.length];
        for (int i = 0; i < XBUP_BLOCKREV_CATALOGPATH.length; i++) {
            path[i] = new Long(XBUP_BLOCKREV_CATALOGPATH[i]);
        }

        XBBlockType contextType = null; // TODO new XBLBlockType(catalog.findBlockTypeByPath(path));
        if (contextType == null) {
            contextType = new XBFixedBlockType();
        } // Empty Context
        return contextType;
    }

    public void clear() {
        setRuleName(null);
        setRules(null);
        setTerminal(null);
        setAltMode(false);
    }

    /**
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * @param ruleName the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return the rules
     */
    public List<String> getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    /**
     * @return the terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * @param terminal the terminal to set
     */
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    /**
     * @return the rangeMode
     */
    public boolean isAltMode() {
        return altMode;
    }

    /**
     * @param rangeMode the rangeMode to set
     */
    public void setAltMode(boolean rangeMode) {
        this.altMode = rangeMode;
    }
}
