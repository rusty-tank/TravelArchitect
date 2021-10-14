package travelarchitect.com.travelarchitect;

/**
 * Created by Russell on 6/2/2018.
 */

public class DisplayScheduleActivityGetSet {
    // Store the id of the  movie poster
    private int mImageDrawable;
    // Store the name of the attration
    private String mName;
    // Store the duration of the attration
    private String mRelease;
    // Store the tags of the attration
    private String mType;
    // Store the time of the attration
    private String mTime;

    // Constructor that is used to create an instance of the  object
    public DisplayScheduleActivityGetSet(int mImageDrawable, String mName, String mRelease, String mType, String mTime) {
        this.mImageDrawable = mImageDrawable;
        this.mName = mName;
        this.mRelease = mRelease;
        this.mType = mType;
        this.mTime = mTime;
    }

    public int getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(int mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmRelease() {
        return mRelease;
    }

    public void setmRelease(String mRelease) {
        this.mRelease = mRelease;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
