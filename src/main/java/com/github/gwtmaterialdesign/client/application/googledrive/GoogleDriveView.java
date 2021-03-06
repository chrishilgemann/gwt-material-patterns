package com.github.gwtmaterialdesign.client.application.googledrive;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.gwtmaterialdesign.client.application.googledrive.collapsible.DriveCollapsible;
import com.github.gwtmaterialdesign.client.dto.DataHelper;
import com.github.gwtmaterialdesign.client.dto.DriveDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.client.ui.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleDriveView extends ViewImpl implements GoogleDrivePresenter.MyView {
    private boolean toggle = true;

    interface Binder extends UiBinder<Widget, GoogleDriveView> {
    }

    @UiField
    MaterialNavBar appNav, searchNav;

    @UiField
    MaterialSearch search;

    @UiField
    MaterialCollapsible driveColaps;

    @UiField
    MaterialIcon iconViewFile;

    @UiField
    MaterialLabel lblViewFile;

    @UiField
    MaterialPanel mainPanel, emptyState;

    @UiField
    MaterialRow mainHeader, rightPanel;

    @UiField
    MaterialSplashScreen splash;

    @Inject
    GoogleDriveView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        splash.show();
        Timer t = new Timer() {
            @Override
            public void run() {
                splash.hide();
            }
        };
        t.schedule(5000);
        search.addCloseHandler(event -> {
            appNav.setVisible(true);
            searchNav.setVisible(false);
        });
        search.addKeyUpHandler(event -> {
            List<DriveDTO> filteredFiles = DataHelper.getAllDrives().stream().filter(dto -> dto.getFileName().toLowerCase().contains(search.getText().toLowerCase())).collect(Collectors.toList());
            populateFiles(filteredFiles);
        });
        populateFiles(DataHelper.getAllDrives());
    }

    public void setViewInfo(DriveDTO dto) {
        lblViewFile.setText(dto.getFileName());
        iconViewFile.setIconType(dto.getIcon());
    }

    @UiHandler("btnSearch")
    void onSearch(ClickEvent e) {
        appNav.setVisible(false);
        searchNav.setVisible(true);
    }

    @UiHandler("iconInfo")
    void onInfo(ClickEvent e) {
        if(toggle){
            // show the right nav
            mainHeader.setRight(334);
            mainPanel.setGrid("l9 m9 s12");
            rightPanel.setRight(0);
            toggle = false;
        }else{
            hidePanel();
        }
    }

    @UiHandler("iconClose")
    void onClose(ClickEvent e) {
        hidePanel();
    }

    private void hidePanel() {
        // hide the right nav
        mainHeader.setRight(0);
        mainPanel.setGrid("l12 s12 m12");
        rightPanel.setRight(-374);
        toggle = true;
    }

    @UiHandler("sortFileName")
    void onSortFileName(ClickEvent e) {
        List<DriveDTO> sortedFiles = DataHelper.getAllDrives();
        Collections.sort(sortedFiles, (o1, o2) -> o1.getFileName().compareToIgnoreCase(o2.getFileName()));
        populateFiles(sortedFiles);
    }

    @UiHandler("sortSharedBy")
    void onSortSharedBy(ClickEvent e) {
        List<DriveDTO> sortedFiles = DataHelper.getAllDrives();
        Collections.sort(sortedFiles, (o1, o2) -> o1.getOwner().compareToIgnoreCase(o2.getOwner()));
        populateFiles(sortedFiles);
    }

    @UiHandler("sortSharedDate")
    void onSortSharedDate(ClickEvent e) {
        List<DriveDTO> sortedFiles = DataHelper.getAllDrives();
        Collections.sort(sortedFiles, (o1, o2) -> o1.getDate().compareToIgnoreCase(o2.getDate()));
        populateFiles(sortedFiles);
    }

    /**
     * Populate all files with given list of files
     * @param allFiles
     */
    private void populateFiles(List<DriveDTO> allFiles) {
        driveColaps.clear();
                for(DriveDTO dto : allFiles) {
            driveColaps.add(new DriveCollapsible(this, dto));
        }
        if(allFiles.size() > 0){
            emptyState.setVisible(false);
            mainPanel.setVisible(true);
            rightPanel.setVisible(true);
            setViewInfo(DataHelper.getAllDrives().get(0));
        }else {
            emptyState.setVisible(true);
            mainPanel.setVisible(false);
            rightPanel.setVisible(false);
        }
    }

}
