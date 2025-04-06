import { ref } from 'vue';
import axios from 'axios';

export function purchaseApprovalService() {
    const form = ref({
        personalId: '',
        amount: '',
        paymentPeriodInMonths: ''
    });

    const loading = ref(false);
    const showModal = ref(false);
    const result = ref({});

    const submitForm = async () => {
        loading.value = true;

        try {
            const response = await axios.post(
                `${import.meta.env.VITE_API_URL}/api/v1/purchase/approval`,
                form.value
            );
            result.value = response.data;
        } catch (error) {
            console.log(error);
            result.value = error.response.data;
        } finally {
            loading.value = false;
            showModal.value = true;
        }
    };

    const closeModal = () => {
        showModal.value = false;
    };

    return {
        form,
        loading,
        showModal,
        result,
        submitForm,
        closeModal
    };
}
