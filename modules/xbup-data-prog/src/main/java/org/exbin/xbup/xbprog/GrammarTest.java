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
package org.exbin.xbup.xbprog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import org.exbin.xbup.catalog.XBAECatalog;
import org.exbin.xbup.catalog.entity.manager.XBEXDescManager;
import org.exbin.xbup.catalog.entity.manager.XBEXLangManager;
import org.exbin.xbup.catalog.entity.manager.XBEXNameManager;
import org.exbin.xbup.core.catalog.base.manager.XBCXDescManager;
import org.exbin.xbup.core.catalog.base.manager.XBCXLangManager;
import org.exbin.xbup.core.catalog.base.manager.XBCXNameManager;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.token.pull.XBPullReader;
import org.exbin.xbup.core.serial.token.XBPullProviderSerialHandler;
import org.exbin.xbup.xbprog.grammar.XBBNFGrammar;
import org.exbin.xbup.xbprog.grammar.XBBNFGrammarRule;
import org.exbin.xbup.xbprog.grammar.XBRegularGrammar;
import org.exbin.xbup.xbprog.grammar.XBRegularGrammarRule;

/**
 * Testing application for Grammars.
 *
 * @version 0.1.19 2010/02/12
 * @author ExBin Project (http://exbin.org)
 */
public class GrammarTest {

    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("XBMathLibPU");
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.AUTO);
        XBAECatalog catalog = new XBAECatalog(em);
        catalog.initCatalog();
        catalog.addCatalogManager(XBCXLangManager.class, new XBEXLangManager(catalog));
        catalog.addCatalogManager(XBCXNameManager.class, new XBEXNameManager(catalog));
        catalog.addCatalogManager(XBCXDescManager.class, new XBEXDescManager(catalog));
//        XBCUpdatePHPHandler wsHandler = new XBCUpdatePHPHandler(catalog);
//        wsHandler.init();
//        wsHandler.getPort().getLanguageId("en");
//        catalog.setUpdateHandler(wsHandler);

        //
        XBRegularGrammar regularGrammar = new XBRegularGrammar();
        XBRegularGrammarRule rule = new XBRegularGrammarRule();
        regularGrammar.getRules().add(rule);
        rule = new XBRegularGrammarRule();
        rule.setLeftNonterminal(1);
        rule.setRightNonterminal(2);
        rule.setRightTerminal(1);
        regularGrammar.getRules().add(rule);
//        XBFileOutputStream fileOutputStream;
//        XBOMOutputStream buffer;
        //XBTreeNode
        try {
//            buffer = new XBOMOutputStream(node);
//            System.out.println("Writing output.xb using level 0");
//            fileOutputStream = new XBFileOutputStream("output.xb");
//            XBEventListenerSerialHandler handler = new XBEventListenerSerialHandler();
//            handler.attachXBEventListener(fileOutputStream);
//            regularGrammar.serializeXB(XBSerializationType.TO_XB, 0, handler);
//            fileOutputStream.close();
//            System.out.println("Writing output_cat.xb using level 1");
//            fileOutputStream = new XBFileOutputStream("output_cat.xb");
//            XBCDeclaration declaration = (XBCDeclaration) regularGrammar.getXBDeclaration();
////            ((XBAECatalog) catalog).
//            Long[] path = declaration.getContextFormat().getCatalogPath().getLongPath();
//            Long[] myPath = new Long[path.length-1];
//            System.arraycopy(path, 0, myPath, 0, path.length-1);
//            catalog.getUpdateHandler().updateFormatSpec(myPath, path[path.length-1]);
//            declaration.setCatalog(catalog);
//            XBTEncapsulator encapsulator = new XBTEncapsulator(new XBContext(catalog, declaration));
//            encapsulator.attachXBTListener(new XBTEventListenerToListener(new XBTToXBEventConvertor(fileOutputStream)));
//
//            XBTEventListenerSerialHandler handler2 = new XBTEventListenerSerialHandler();
//            handler2.attachXBTEventListener(new XBTEventOutputStream(new XBTListenerToEventListener(encapsulator)));
//            regularGrammar.serializeXB(XBSerializationType.TO_XB, 1, handler);
//            fileOutputStream.close();
        } catch (XBProcessingException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        regularGrammar = new XBRegularGrammar();
        InputStream fileInputStream;
//        XBOMOutputStream buffer;
        //XBTreeNode
        try {
//            buffer = new XBOMOutputStream(node);
            fileInputStream = new FileInputStream("output.xb");
            XBPullProviderSerialHandler handler = new XBPullProviderSerialHandler();
            handler.attachXBPullProvider(new XBPullReader(fileInputStream));
//            regularGrammar.serializeXB(XBSerializationType.FROM_XB, 0, handler);
            // new XBTEncapsulator(regularGrammar.getXBDeclaration(), XBTDefaultEventListener.toXBListener(stream))
            fileInputStream.close();
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void test2() {

        XBBNFGrammar grammar = new XBBNFGrammar();
        XBBNFGrammarRule startRule = new XBBNFGrammarRule();
        startRule.setRuleName("start");

        XBBNFGrammarRule nSampleRule = new XBBNFGrammarRule();
        nSampleRule.setRuleName("nSample");
        List<String> subRules = new ArrayList<>();
        subRules.add(nSampleRule.getRuleName());
        nSampleRule.setRules(subRules);

        XBBNFGrammarRule terminalSampleRule = new XBBNFGrammarRule();
        terminalSampleRule.setRuleName("terminalSample");
        terminalSampleRule.setTerminal("terminal");

        subRules = new ArrayList<>();
        subRules.add(nSampleRule.getRuleName());
        subRules.add(terminalSampleRule.getRuleName());
        startRule.setRules(subRules);
        grammar.addRule(startRule);
        grammar.addRule(nSampleRule);
        grammar.addRule(terminalSampleRule);

        System.out.print(grammar.generateGrammarDefinition());
    }
}
