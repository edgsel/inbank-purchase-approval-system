<script setup>
import { usePurchaseApproval } from './scripts/usePurchaseApproval.js';

const {
  form,
  loading,
  showModal,
  result,
  submitForm,
  closeModal
} = usePurchaseApproval();
</script>

<template>
  <h1>Purchase Approval System</h1>
  <div class="app-container">
    <form @submit.prevent="submitForm">
      <h2>Purchase Approval Request</h2>

      <label>Customer Personal ID:</label>
      <input v-model="form.personalId" type="number" required/>

      <label>Amount (€):</label>
      <input v-model="form.amount" type="number" required/>

      <label>Payment Period (months):</label>
      <input v-model="form.paymentPeriodInMonths" type="number" required/>

      <button type="submit" :disabled="loading">Submit Request</button>

      <div v-if="loading" class="loader"></div>
    </form>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <h3>Request Result</h3>
        <p v-if="result.approved">
          Approved!<br>
          Amount: €{{ result.amount }}<br>
          Payment Period: {{ result.paymentPeriodInMonths }} months
        </p>
        <p v-else>
          Unfortunately, your request has been rejected.
        </p>
        <button @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>
