package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	private Department department;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	@FXML
	private void onButtonSaveAction(ActionEvent event) {
		if (department == null) {
			throw new IllegalStateException("Null department");
		}
		if (departmentService == null) {
			throw new IllegalStateException("Null department");
		}
		try {
			department = getFormData();
			departmentService.saveOrUpdate(department);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Erro saving department", null, e.getMessage(), AlertType.ERROR);
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErros());
		}
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}	
	}

	private Department getFormData() {
		
		ValidationException exception = new ValidationException("Validation erro");
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErro("name", "Field can't be empty");
		}
		
		Department department = new Department(Utils.tryParceToInt(txtId.getText()), txtName.getText());
		
		if(exception.getErros().size() > 0) {
			throw exception;
		}
		
		return department;
	}

	@FXML
	private void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void setDepertment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	public void updateFormData() {
		if (department == null) {
			throw new IllegalStateException("Null department");
		}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
