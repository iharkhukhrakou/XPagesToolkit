/*
 * � Copyright WebGate Consulting AG, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.xpt.core.dss.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openntf.xpt.core.dss.changeLog.ChangeLogService;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class Java2DominoBinder {

	private ArrayList<Definition> m_Definition;

	public Java2DominoBinder() {
		m_Definition = new ArrayList<Definition>();
	}

	public void addDefinition(String strNotesField, String strJavaField, IBinder<?> binCurrent, boolean changeLog, HashMap<String, Object> addValues, boolean encrypted, String[] encRoles) {
		m_Definition.add(new Definition(strNotesField, strJavaField, binCurrent, changeLog, addValues, encrypted, encRoles));
	}

	public void processDocument(Document docProcess, Object objCurrent) throws NotesException {
		for (Iterator<Definition> itDefinition = m_Definition.iterator(); itDefinition.hasNext();) {
			Definition defCurrent = itDefinition.next();
			Object[] arrResult = null;
			///Entweder hier betreffend Encryption checken == f�r jede Property Encryption checken || Im EncryptionBinder. If f�llt weg aber 2 neue Parameter (encrypted und encroles) Was ist performanter? :)
			if(defCurrent.isEncrypted() || (defCurrent.isEncrypted())){ /// AND USER IS IN ENCROLES)
				arrResult = defCurrent.getBinder().processJava2Domino(docProcess, objCurrent, defCurrent.getNotesField(), defCurrent.getJavaField(),
					defCurrent.getAdditionalValues());
			}
			if (defCurrent.isChangeLog() && arrResult != null) {
				ChangeLogService.getInstance().checkChangeLog(objCurrent, arrResult[0], arrResult[1], defCurrent.getJavaField(), defCurrent.getNotesField());
			}

		}
	}
}
