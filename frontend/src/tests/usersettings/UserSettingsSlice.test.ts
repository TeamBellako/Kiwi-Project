import {
    fetchUserSettings,
    initialState,
    updateUserSettings,
    userSettingsReducer
} from "../../usersettings/UserSettingsSlice";
import {UserSettings} from "../../usersettings/UserSettings";

const mockSettings: UserSettings = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'dark',
};

describe('UserSettings Slice Tests', () => {
    describe('General Tests', () => {
        it('slice loads and shows initial state', () => {
            expect(userSettingsReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });
    })
    
    describe('Fetch Tests', () => {
        it('slice transitions to loading on fetch', () => {
            const nextState = userSettingsReducer(
                initialState,
                fetchUserSettings.pending('', undefined)
            );
            
            expect(nextState.status).toBe('loading');
            expect(nextState.error).toBeNull();
        });
    
        it('slice transitions to succeeded on fetch success', () => {
            const nextState = userSettingsReducer(
                initialState,
                fetchUserSettings.fulfilled(mockSettings, '', undefined)
            );
            
            expect(nextState.status).toBe('succeeded');
            expect(nextState.userSettings).toEqual(mockSettings);
            expect(nextState.error).toBeNull();
        });
    
        it('slice transitions to failed on fetch failure', () => {
            const nextState = userSettingsReducer(
                initialState,
                fetchUserSettings.rejected(new Error('Failed'), '', undefined, 'Failed')
            );
            
            expect(nextState.status).toBe('failed');
            expect(nextState.error).toBe('Failed');
        });
    })

    describe('Update Tests', () => {
        it('slice transitions to loading on update', () => {
            const nextState = userSettingsReducer(
                initialState,
                updateUserSettings.pending('', mockSettings)
            );
            
            expect(nextState.status).toBe('loading');
        });
    
        it('slice transitions to succeeded on update success', () => {
            const nextState = userSettingsReducer(
                initialState,
                updateUserSettings.fulfilled(mockSettings, '', mockSettings)
            );
            
            expect(nextState.status).toBe('succeeded');
            expect(nextState.userSettings).toEqual(mockSettings);
        });
    
        it('slice transitions to failed on update failure', () => {
            const nextState = userSettingsReducer(
                initialState,
                updateUserSettings.rejected(new Error('Update failed'), '', mockSettings, 'Update failed')
            );
            
            expect(nextState.status).toBe('failed');
            expect(nextState.error).toBe('Update failed');
        });
    })
});