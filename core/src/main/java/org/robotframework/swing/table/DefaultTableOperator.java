/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *
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
 */

package org.robotframework.swing.table;

import java.awt.Point;

import javax.swing.JTable;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.robotframework.swing.arguments.IdentifierHandler;
import org.springframework.util.ObjectUtils;

/**
 * @author Heikki Hulkko
 */
public class DefaultTableOperator extends JTableOperator implements TableOperator {
    public DefaultTableOperator(JTable table) {
        super(table);
    }

    public DefaultTableOperator(ContainerOperator context, int index) {
        super(context, index);
    }

    public DefaultTableOperator(ContainerOperator context, ComponentChooser componentChooser) {
        super(context, componentChooser);
    }

    public Object getValueAt(String rowIdentifier, String columnIdentifier) {
        Point coordinates = findCell(rowIdentifier, columnIdentifier);
        return getValueAt(coordinates.y, coordinates.x);
    }

    public boolean isCellSelected(String rowIdentifier, String columnIdentifier) {
        Point coordinates = findCell(rowIdentifier, columnIdentifier);
        return isCellSelected(coordinates.y, coordinates.x);
    }

    public void selectCell(String rowIdentifier, String columnIdentifier) {
        Point coordinates = findCell(rowIdentifier, columnIdentifier);
        selectCell(coordinates.y, coordinates.x);
    }

    public void setValueAt(Object newValue, String rowIdentifier, String columnIdentifier) {
        Point coordinates = findCell(rowIdentifier, columnIdentifier);
        setValueAt(newValue, coordinates.y, coordinates.x);
    }
    
    public void changeCellObject(String row, String columnIdentifier, String newValue) {
        Point coordinates = findCell(row, columnIdentifier);
        changeCellObject(coordinates.y, coordinates.x, newValue);
    }

    public Point findCell(String row, String columnIdentifier) {
        TableCellChooser cellChooser = createCellChooser(row, columnIdentifier);
        Point cell = findCell(cellChooser);
        if (cellIsInvalid(cell))
            throw new InvalidCellException(row, columnIdentifier);
        return cell;
    }

    private boolean cellIsInvalid(Point cell) {
        return cell.x < 0 || cell.y < 0;
    }

    private TableCellChooser createCellChooser(String row, String columnIdentifier) {
        return new CellChooserFactory(row).createCellChooser(columnIdentifier);
    }

    private Object getColumHeader(int columnIndex) {
        return getColumnModel().getColumn(columnIndex).getHeaderValue();
    }

    private class CellChooserFactory extends IdentifierHandler<TableCellChooser> {
        private int row;

        public CellChooserFactory(String rowAsString) {
            row = Integer.parseInt(rowAsString);
        }

        public TableCellChooser indexArgument(final int column) {
            return new AbstractTableCellChooser(row) {
                protected boolean checkColumn(int index) {
                    return column == index;
                }
            };
        }

        public TableCellChooser nameArgument(final String columnHeader) {
            return new AbstractTableCellChooser(row) {
                protected boolean checkColumn(int columnIndex) {
                    return ObjectUtils.nullSafeEquals(columnHeader, getColumHeader(columnIndex));
                }
            };
        }

        public TableCellChooser createCellChooser(String columnIdentifier) {
            return parseArgument(columnIdentifier);
        }
    }
}