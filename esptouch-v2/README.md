# EspTouch V2


## APIs
- Create provisioner instance
  ```java
  Context context; // Set Application Context
  EspProvisioner provisioner = new EspProvisioner(context);
  ```

- Start send Sync packets
  ```java
  EspSyncListener listener = new EspSyncListener() {
      @Override
      public void onStart() {
      }

      @Override
      public void onStop() {
      }

      @Override
      public void onError(Exception e) {
      }
  };
  provisioner.startSync(listener); // listener is nullable
  ```

- Stop send Sync packets
  ```java
  provisioner.stopSync();
  ```

- Start provisioning
    - Provisioning task will run for 90 seconds
  ```java
  Context context; // Set Application Context
  EspProvisioningRequest request = new EspProvisioningRequest.Builder(context)
                  .setSSID(ssid) // AP's SSID, nullable
                  .setBSSID(bssid) // AP's BSSID, nullable
                  .setPassword(password) // AP's password, nullable if the AP is open
                  .setReservedData(customData) // User's custom data, nullable. If not null, the max length is 64
                  .setAESKey(aesKey) // nullable, if not null, it must be 16 bytes. App developer should negotiate an AES key with Device developer first.
                  .build();
  EspProvisioningListener listener = new EspProvisioningListener() {
      @Override
      public void onStart() {
      }

      @Override
      public void onResponse(EspProvisionResult result) {
          // Result callback
      }

      @Override
      public void onStop() {
      }

      @Override
      public void onError(Exception e) {
      }
  };
  provisioner.startProvisioning(request, listener); // request is nonnull, listener is nullable
  ```

- Stop provisioning
  ```java
  provisioner.stopProvisioning();
  ```

- Close provisioner instance
    - It is necessary to close the provisioner to release the resources
  ```java
  provisioner.close()
  ```
