package org.openntf.xpt.objectlist.datasource;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openntf.xpt.core.XPTRuntimeException;
import org.openntf.xpt.core.dss.DominoStorageService;
import org.openntf.xpt.core.dss.annotations.XPTPresentationControl;
import org.openntf.xpt.core.utils.RoleAndGroupProvider;
import org.openntf.xpt.core.utils.ServiceSupport;

import com.ibm.xsp.model.DataObject;

public class DSSObject implements DataObject, Serializable {

	private static final long serialVersionUID = 1L;
	private final Object m_BO;
	private final Map<String, Class<?>> m_Fields = new HashMap<String, Class<?>>();
	private final boolean m_EditMode;
	private final boolean m_IsNew;

	public DSSObject(Object obj, boolean editMode, boolean isNew) {
		m_BO = obj;
		m_EditMode = editMode;
		m_IsNew = isNew;
	}

	@Override
	public Class<?> getType(Object field) {
		String elField = "" + field;
		if (m_Fields.containsKey(elField)) {
			return m_Fields.get(elField);
		}
		String strMethode = ServiceSupport.makeGetter(elField);
		try {
			Method mt = m_BO.getClass().getMethod(strMethode);
			m_Fields.put(elField, mt.getReturnType());
			return mt.getReturnType();
		} catch (Exception ex) {
			throw new XPTRuntimeException("Error during getType for " + elField, ex);
		}
	}

	@Override
	public Object getValue(Object field) {
		String elField = "" + field;
		String strMethode = ServiceSupport.makeGetter(elField);
		try {
			Method mt = m_BO.getClass().getMethod(strMethode);
			m_Fields.put(elField, mt.getReturnType());
			return mt.invoke(m_BO);
		} catch (Exception ex) {
			throw new XPTRuntimeException("Error during getValue for " + elField, ex);
		}

	}

	@Override
	public boolean isReadOnly(Object field) {
		if (m_EditMode) {
			try {
				XPTPresentationControl ctrl = DominoStorageService.getInstance().getXPTPresentationControl(m_BO, "" + field);
				if (ctrl == null) {
					return false;
				} else {
					if (ctrl.editNewOnly() && !m_IsNew) {
						return true;
					}
					if (ctrl.modifyOnly() && m_IsNew) {
						return true;
					}
					if (ctrl.editRoles().length == 0) {
						return false;
					} else {
						List<String> lstEditors = Arrays.asList(ctrl.editRoles());
						for (String strRole : RoleAndGroupProvider.getInstance().getMyGroupsAndRoles()) {
							if (lstEditors.contains(strRole)) {
								return false;
							}
						}
					}
					return true;
				}
			} catch (Exception ex) {
				throw new XPTRuntimeException("Error curing isReadOnly for " + field, ex);
			}
		} else {
			return true;
		}
	}

	@Override
	public void setValue(Object field, Object arg1) {
		String elField = "" + field;
		try {
			Class<?> setterClass = getType(field);
			String strMethode = ServiceSupport.makeSetter(elField);
			Method mt = m_BO.getClass().getMethod(strMethode, setterClass);
			mt.invoke(m_BO, arg1);
		} catch (Exception ex) {
			throw new XPTRuntimeException("Error during setValue for " + elField, ex);
		}
	}

	public Object getBO() {
		return m_BO;
	}

}
