package mohammedyouser.com.mustaemalaleppo.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModel_Item extends ViewModel {
    private final MutableLiveData<String[]> selectedItem = new MutableLiveData<>();
    public void selectItem(String[] item) {
        selectedItem.setValue(new String[]{"",""});
    }
    public LiveData<String[]> getSelectedItem() {
        return selectedItem;
    }
}