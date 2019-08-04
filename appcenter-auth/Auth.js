// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

const ReactNative = require('react-native');

const { AppCenterReactNativeAuth } = ReactNative.NativeModules;

const Auth = {
    isEnabled,
    setEnabled,
    signIn,
    signOut
};

/**
 * Check whether Auth service is enabled or not.
 *
 * @return Promise with the result being true if enabled, false otherwise.
 */
function isEnabled() {
    return AppCenterReactNativeAuth.isEnabled();
}

/**
 * Enable or disable Auth service.
 *
 * @param {boolean} enabled - true to enable, false to disable.
 * @return Promise with null result to monitor when the operation completes.
 */
function setEnabled(enabled) {
    return AppCenterReactNativeAuth.setEnabled(enabled);
}

/**
 * Sign in to get user information.
 *
 * @return Promise with the result of the sign-in operation.
 */
function signIn() {
    return AppCenterReactNativeAuth.signIn();
}

/**
 * Sign out user and invalidate a user's token.
 */
function signOut() {
    AppCenterReactNativeAuth.signOut();
}

module.exports = Auth;
