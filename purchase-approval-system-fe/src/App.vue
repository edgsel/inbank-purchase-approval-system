<script setup>
import { purchaseApprovalService } from './scripts/purchaseApprovalService.js';
import { getStatusLabel } from './scripts/helper.js';

const {
  form,
  loading,
  showModal,
  result,
  submitForm,
  closeModal
} = purchaseApprovalService();
</script>

<template>
  <h1>Inbank Purchase Approval System</h1>
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
        <h3>Approval Result</h3>
        <p>Status: {{ getStatusLabel(result.approved) }}</p>
        <div v-if="!result.description">
          <p v-if="result.amount">Amount: €{{ result.amount }}</p>
          <p v-if="result.paymentPeriodInMonths">Payment Period: {{ result.paymentPeriodInMonths }} months</p>
        </div>
        <p v-if="result.description" ><b>Reason</b>: {{ result.description }}</p>
        <button @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>
