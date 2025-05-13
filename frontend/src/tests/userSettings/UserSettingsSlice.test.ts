import {userSettingsReducer} from "../../userSettings/store/UserSettingsSlice";
import {loadUserSettings, updateUserSettingsDTO} from "../../userSettings/store/UserSettingsThunks"
import {validUserSettingsDTO} from "./UserSettingsTestFactory";
import {initialState} from "../../userSettings/types/UserSettingsState";

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
                loadUserSettings.pending('', undefined)
            );
            
            expect(nextState.status).toBe('loading');
            expect(nextState.error).toBeNull();
        });
    
        it('slice transitions to succeeded on fetch success', () => {
            const nextState = userSettingsReducer(
                initialState,
                loadUserSettings.fulfilled(validUserSettingsDTO, '', undefined)
            );
            
            expect(nextState.status).toBe('succeeded');
            expect(nextState.userSettings).toEqual(validUserSettingsDTO);
            expect(nextState.error).toBeNull();
        });
    
        it('slice transitions to failed on fetch failure', () => {
            const errorMessage = 'Failed to fetch'
            const nextState = userSettingsReducer(
                initialState,
                loadUserSettings.rejected(new Error(errorMessage), '', undefined, errorMessage)
            );
            
            expect(nextState.status).toBe('failed');
        });
    })

    describe('Update Tests', () => {
        it('slice transitions to succeeded on update success', () => {
            const nextState = userSettingsReducer(
                initialState,
                updateUserSettingsDTO.fulfilled(validUserSettingsDTO, '', validUserSettingsDTO)
            );
            
            expect(nextState.status).toBe('succeeded');
            expect(nextState.userSettings).toEqual(validUserSettingsDTO);
        });
    
        it('slice transitions to failed on update failure', () => {
            const errorMessage = 'Failed to update'
            const nextState = userSettingsReducer(
                initialState,
                updateUserSettingsDTO.rejected(new Error(errorMessage), '', validUserSettingsDTO, errorMessage)
            );

            expect(nextState.status).toBe('failed');
            expect(nextState.error).toBe(errorMessage);
        });
    })
});